package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.CustomerNotVerifiedException;
import com.novabank.backend.exception.InvalidAccountStatusException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.AccountNumberGeneratorService;
import com.novabank.backend.service.AccountService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Account} entities.
 * Enforces business constraints such as compliance validation and primary account locks.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountNumberGeneratorService generatorService;

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Request to create account of type: {} for customer ID: {}", request.getAccountType(), request.getCustomerId());
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));

        // Rule 1: Only verified (ACTIVE) customers can open accounts
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotVerifiedException("Customer profile status is not ACTIVE. KYC verification is required.");
        }

        // Rule 3: Customer cannot have duplicate primary savings account
        if (request.isPrimary() && request.getAccountType() == AccountType.SAVINGS) {
            Optional<Account> primaryOpt = accountRepository.findByCustomerAndAccountTypeAndIsPrimary(customer, AccountType.SAVINGS, true);
            if (primaryOpt.isPresent()) {
                throw new BadRequestException("Customer already has a designated primary savings account.");
            }
        }

        // Generate identifiers
        String accountNumber = generatorService.generateAccountNumber();
        String ifscCode = generatorService.generateIfscCode(request.getBranchCode());

        Account account = Account.builder()
                .customer(customer)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(request.getInitialBalance())
                .availableBalance(request.getInitialBalance())
                .currency(request.getCurrency())
                .branchCode(request.getBranchCode())
                .ifscCode(ifscCode)
                .status(AccountStatus.ACTIVE) // Default to ACTIVE upon opening
                .openedDate(LocalDate.now())
                .isPrimary(request.isPrimary())
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Bank account opened successfully: Account Number: {}", accountNumber);
        return convertToAccountResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
        return convertToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByAccountNumber(String number) {
        Account account = accountRepository.findByAccountNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + number));
        return convertToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AccountResponse> searchAccounts(
            int page, int size, String sortBy, String sortDir,
            AccountType type, AccountStatus status, LocalDate openedDate,
            String accountNumber, UUID customerId
    ) {
        log.info("Searching accounts with page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Account> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(cb.equal(root.get("accountType"), type));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (openedDate != null) {
                predicates.add(cb.equal(root.get("openedDate"), openedDate));
            }
            if (accountNumber != null && !accountNumber.isBlank()) {
                predicates.add(cb.equal(root.get("accountNumber"), accountNumber));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Account> accountPage = accountRepository.findAll(spec, pageable);
        return new PagedResponse<>(accountPage.map(this::convertToAccountResponse));
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request) {
        log.info("Updating account ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        // Rule 4: Closed account cannot be modified
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is CLOSED and cannot be modified.");
        }

        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
            if (request.getStatus() == AccountStatus.CLOSED) {
                account.setClosedDate(LocalDate.now());
                account.setPrimary(false);
            }
        }

        if (request.getIsPrimary() != null && request.getIsPrimary()) {
            setPrimaryLogic(account);
        }

        Account updatedAccount = accountRepository.save(account);
        return convertToAccountResponse(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponse freezeAccount(UUID id) {
        log.info("Freezing account ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Closed accounts cannot be frozen.");
        }

        account.setStatus(AccountStatus.FROZEN);
        Account saved = accountRepository.save(account);
        return convertToAccountResponse(saved);
    }

    @Override
    @Transactional
    public AccountResponse activateAccount(UUID id) {
        log.info("Activating account ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Closed accounts cannot be reactivated.");
        }

        account.setStatus(AccountStatus.ACTIVE);
        Account saved = accountRepository.save(account);
        return convertToAccountResponse(saved);
    }

    @Override
    @Transactional
    public AccountResponse closeAccount(UUID id) {
        log.info("Closing account ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is already closed.");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedDate(LocalDate.now());
        account.setPrimary(false);

        Account saved = accountRepository.save(account);
        return convertToAccountResponse(saved);
    }

    @Override
    @Transactional
    public AccountResponse setPrimaryAccount(UUID id) {
        log.info("Setting account ID: {} as primary", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Closed accounts cannot be designated as primary.");
        }
        if (account.getAccountType() != AccountType.SAVINGS) {
            throw new BadRequestException("Only SAVINGS accounts can be set as primary.");
        }

        setPrimaryLogic(account);
        Account saved = accountRepository.save(account);
        return convertToAccountResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountSummaryResponse> getAccountsByCustomerId(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        List<Account> accounts = accountRepository.findByCustomer(customer);
        return accounts.stream()
                .map(acc -> AccountSummaryResponse.builder()
                        .id(acc.getId())
                        .accountNumber(acc.getAccountNumber())
                        .accountType(acc.getAccountType())
                        .balance(acc.getBalance())
                        .currency(acc.getCurrency())
                        .status(acc.getStatus())
                        .isPrimary(acc.isPrimary())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse convertToAccountResponse(Account account) {
        if (account == null) {
            return null;
        }

        String customerFullName = (account.getCustomer() != null)
                ? String.format("%s %s", account.getCustomer().getFirstName(), account.getCustomer().getLastName())
                : null;

        UUID customerIdVal = (account.getCustomer() != null) ? account.getCustomer().getId() : null;

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(customerIdVal)
                .customerName(customerFullName)
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .currency(account.getCurrency())
                .branchCode(account.getBranchCode())
                .ifscCode(account.getIfscCode())
                .status(account.getStatus())
                .openedDate(account.getOpenedDate())
                .closedDate(account.getClosedDate())
                .isPrimary(account.isPrimary())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    private void setPrimaryLogic(Account account) {
        // Find existing primary savings account for this customer
        Optional<Account> currentPrimaryOpt = accountRepository.findByCustomerAndAccountTypeAndIsPrimary(
                account.getCustomer(), AccountType.SAVINGS, true
        );

        if (currentPrimaryOpt.isPresent()) {
            Account currentPrimary = currentPrimaryOpt.get();
            if (!currentPrimary.getId().equals(account.getId())) {
                currentPrimary.setPrimary(false);
                accountRepository.save(currentPrimary);
                log.info("Reset primary status on account ID: {}", currentPrimary.getId());
            }
        }
        account.setPrimary(true);
    }
}
