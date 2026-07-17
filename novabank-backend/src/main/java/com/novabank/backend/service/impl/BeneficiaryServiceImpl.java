package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Beneficiary;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.RelationshipType;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.DuplicateBeneficiaryException;
import com.novabank.backend.exception.OwnAccountNotAllowedException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.BeneficiaryRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.BeneficiaryService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing {@link Beneficiary} entities.
 * Enforces business logic such as self-account addition checks and duplicates validation.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final CustomerRepository customerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public BeneficiaryResponse addBeneficiary(User user, CreateBeneficiaryRequest request) {
        log.info("Request to add beneficiary: {} for user: {}", request.getBeneficiaryName(), user.getEmail());
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        // Rule 3: Customer cannot add own account as beneficiary
        Optional<Account> ownAccountOpt = accountRepository.findByAccountNumber(request.getBeneficiaryAccountNumber());
        if (ownAccountOpt.isPresent() && ownAccountOpt.get().getCustomer().getId().equals(customer.getId())) {
            throw new OwnAccountNotAllowedException("Own bank accounts cannot be added as transfer beneficiaries.");
        }

        // Rule 2: Duplicate beneficiary account numbers are not allowed (per customer)
        if (beneficiaryRepository.existsByCustomerAndBeneficiaryAccountNumberAndStatusNot(
                customer, request.getBeneficiaryAccountNumber(), BeneficiaryStatus.DELETED)) {
            throw new DuplicateBeneficiaryException("A beneficiary with this account number is already saved on your profile.");
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .customer(customer)
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryAccountNumber(request.getBeneficiaryAccountNumber())
                .beneficiaryBankName(request.getBeneficiaryBankName())
                .beneficiaryIfscCode(request.getBeneficiaryIfscCode())
                .nickname(request.getNickname())
                .relationship(request.getRelationship())
                .isFavorite(request.isFavorite())
                .status(BeneficiaryStatus.ACTIVE) // Defaults to ACTIVE on addition
                .build();

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        log.info("Beneficiary added successfully with ID: {}", saved.getId());
        return convertToBeneficiaryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponse getBeneficiaryById(UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .filter(b -> b.getStatus() != BeneficiaryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));
        return convertToBeneficiaryResponse(beneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BeneficiaryResponse> searchBeneficiaries(
            int page, int size, String sortBy, String sortDir,
            BeneficiaryStatus status, Boolean isFavorite, RelationshipType relationship,
            String name, String accountNumber, String nickname, UUID customerId
    ) {
        log.info("Searching beneficiaries - page: {}, size: {}, customerId: {}", page, size, customerId);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Beneficiary> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter out deleted records by default
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(cb.notEqual(root.get("status"), BeneficiaryStatus.DELETED));
            }

            if (isFavorite != null) {
                predicates.add(cb.equal(root.get("isFavorite"), isFavorite));
            }
            if (relationship != null) {
                predicates.add(cb.equal(root.get("relationship"), relationship));
            }
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("beneficiaryName")), "%" + name.toLowerCase() + "%"));
            }
            if (accountNumber != null && !accountNumber.isBlank()) {
                predicates.add(cb.equal(root.get("beneficiaryAccountNumber"), accountNumber));
            }
            if (nickname != null && !nickname.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nickname")), "%" + nickname.toLowerCase() + "%"));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Beneficiary> pageResult = beneficiaryRepository.findAll(spec, pageable);
        return new PagedResponse<>(pageResult.map(this::convertToBeneficiaryResponse)); // Wait! It should map to convertToBeneficiaryResponse! Let's check: Yes, convertToBeneficiaryResponse!
    }

    @Override
    @Transactional
    public BeneficiaryResponse updateBeneficiary(UUID id, UpdateBeneficiaryRequest request) {
        log.info("Updating beneficiary ID: {}", id);
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .filter(b -> b.getStatus() != BeneficiaryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));

        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setBeneficiaryBankName(request.getBeneficiaryBankName());
        beneficiary.setBeneficiaryIfscCode(request.getBeneficiaryIfscCode());
        beneficiary.setNickname(request.getNickname());
        beneficiary.setRelationship(request.getRelationship());
        if (request.getIsFavorite() != null) {
            beneficiary.setFavorite(request.getIsFavorite());
        }

        Beneficiary updated = beneficiaryRepository.save(beneficiary);
        return convertToBeneficiaryResponse(updated);
    }

    @Override
    @Transactional
    public BeneficiaryResponse toggleFavorite(UUID id, boolean isFavorite) {
        log.info("Toggling favorite to {} for beneficiary ID: {}", isFavorite, id);
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .filter(b -> b.getStatus() != BeneficiaryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));

        beneficiary.setFavorite(isFavorite);
        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return convertToBeneficiaryResponse(saved);
    }

    @Override
    @Transactional
    public BeneficiaryResponse updateStatus(UUID id, BeneficiaryStatus status) {
        log.info("Updating status to {} for beneficiary ID: {}", status, id);
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .filter(b -> b.getStatus() != BeneficiaryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));

        if (status == BeneficiaryStatus.DELETED) {
            throw new BadRequestException("Use DELETE endpoint to soft delete beneficiaries.");
        }

        beneficiary.setStatus(status);
        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return convertToBeneficiaryResponse(saved);
    }

    @Override
    @Transactional
    public void deleteBeneficiary(UUID id) {
        log.info("Soft deleting beneficiary ID: {}", id);
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .filter(b -> b.getStatus() != BeneficiaryStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));

        beneficiary.setStatus(BeneficiaryStatus.DELETED);
        beneficiaryRepository.save(beneficiary);
        log.info("Beneficiary soft deleted successfully: {}", id);
    }

    @Override
    public BeneficiaryResponse convertToBeneficiaryResponse(Beneficiary beneficiary) {
        if (beneficiary == null) {
            return null;
        }
        return BeneficiaryResponse.builder()
                .id(beneficiary.getId())
                .customerId(beneficiary.getCustomer().getId())
                .beneficiaryName(beneficiary.getBeneficiaryName())
                .beneficiaryAccountNumber(beneficiary.getBeneficiaryAccountNumber())
                .beneficiaryBankName(beneficiary.getBeneficiaryBankName())
                .beneficiaryIfscCode(beneficiary.getBeneficiaryIfscCode())
                .nickname(beneficiary.getNickname())
                .relationship(beneficiary.getRelationship())
                .isFavorite(beneficiary.isFavorite())
                .status(beneficiary.getStatus())
                .createdAt(beneficiary.getCreatedAt())
                .updatedAt(beneficiary.getUpdatedAt())
                .build();
    }
}
