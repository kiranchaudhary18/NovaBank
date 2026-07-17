package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Address;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.KycStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AddressRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.CustomerService;
import com.novabank.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Customer} entities.
 * Coordinates user profiles, addresses, photo uploads, and dynamic searches.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public CustomerResponse createProfile(User user, CustomerRequest request) {
        log.info("Creating customer profile for user email: {}", user.getEmail());

        if (customerRepository.existsByUser(user)) {
            throw new BadRequestException("A customer profile already exists for this account.");
        }

        // Validate uniqueness of email and phone
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("A customer profile with this email already exists.");
        }
        if (customerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("A customer profile with this phone number already exists.");
        }

        // Generate customer ID sequence (synchronized)
        String customerId = generateNextCustomerId();

        Customer customer = Customer.builder()
                .user(user)
                .customerId(customerId)
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .maritalStatus(request.getMaritalStatus())
                .occupation(request.getOccupation())
                .annualIncome(request.getAnnualIncome())
                .nationality(request.getNationality())
                .status(CustomerStatus.INACTIVE) // Inactive until KYC is approved
                .build();

        // Save Customer entity first
        Customer savedCustomer = customerRepository.save(customer);

        // Process addresses
        if (request.getAddresses() != null) {
            for (AddressRequest addrReq : request.getAddresses()) {
                Address address = Address.builder()
                        .customer(savedCustomer)
                        .addressType(addrReq.getAddressType())
                        .houseNumber(addrReq.getHouseNumber())
                        .street(addrReq.getStreet())
                        .city(addrReq.getCity())
                        .district(addrReq.getDistrict())
                        .state(addrReq.getState())
                        .country(addrReq.getCountry())
                        .postalCode(addrReq.getPostalCode())
                        .build();
                addressRepository.save(address);
                savedCustomer.addAddress(address);
            }
        }

        log.info("Customer profile created successfully with ID: {} / Customer ID: {}", savedCustomer.getId(), customerId);
        return convertToCustomerResponse(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse updateProfile(User user, CustomerRequest request) {
        log.info("Updating customer profile for user email: {}", user.getEmail());
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        // Validate uniqueness of email and phone, excluding current customer
        if (customerRepository.existsByEmailAndIdNot(request.getEmail(), customer.getId())) {
            throw new BadRequestException("Email is already used by another customer profile.");
        }
        if (customerRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), customer.getId())) {
            throw new BadRequestException("Phone number is already used by another customer profile.");
        }

        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setGender(request.getGender());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setNationality(request.getNationality());

        // Replace addresses
        addressRepository.deleteAll(customer.getAddresses());
        customer.getAddresses().clear();

        if (request.getAddresses() != null) {
            for (AddressRequest addrReq : request.getAddresses()) {
                Address address = Address.builder()
                        .customer(customer)
                        .addressType(addrReq.getAddressType())
                        .houseNumber(addrReq.getHouseNumber())
                        .street(addrReq.getStreet())
                        .city(addrReq.getCity())
                        .district(addrReq.getDistrict())
                        .state(addrReq.getState())
                        .country(addrReq.getCountry())
                        .postalCode(addrReq.getPostalCode())
                        .build();
                addressRepository.save(address);
                customer.addAddress(address);
            }
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer profile updated successfully for ID: {}", updatedCustomer.getId());
        return convertToCustomerResponse(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getMyProfile(User user) {
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));
        return convertToCustomerResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .filter(c -> c.getStatus() != CustomerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return convertToCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse uploadProfilePhoto(User user, MultipartFile file) {
        log.info("Uploading profile photo for user: {}", user.getEmail());
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        String savedFilePath = FileUploadUtil.saveFile("photos", file);
        customer.setProfilePhoto(savedFilePath);

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Profile photo updated successfully for user ID: {}", customer.getId());
        return convertToCustomerResponse(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse searchCustomer(String field, String query) {
        log.info("Searching customer by field: {}, query: {}", field, query);
        Customer customer;
        switch (field.toLowerCase()) {
            case "customerid" -> customer = customerRepository.findByCustomerId(query)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with customerId: " + query));
            case "phone" -> customer = customerRepository.findByPhoneNumber(query)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with phone: " + query));
            case "email" -> customer = customerRepository.findByEmail(query)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + query));
            case "aadhaar" -> customer = customerRepository.findByAadhaarNumber(query)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with Aadhaar number: " + query));
            case "pan" -> customer = customerRepository.findByPanNumber(query)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with PAN: " + query));
            default -> throw new BadRequestException("Invalid search criteria field: " + field);
        }

        if (customer.getStatus() == CustomerStatus.DELETED) {
            throw new ResourceNotFoundException("Customer profile is deleted.");
        }

        return convertToCustomerResponse(customer);
    }

    @Override
    public CustomerResponse convertToCustomerResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        List<AddressResponse> addrList = customer.getAddresses().stream()
                .map(addr -> AddressResponse.builder()
                        .id(addr.getId())
                        .addressType(addr.getAddressType())
                        .houseNumber(addr.getHouseNumber())
                        .street(addr.getStreet())
                        .city(addr.getCity())
                        .district(addr.getDistrict())
                        .state(addr.getState())
                        .country(addr.getCountry())
                        .postalCode(addr.getPostalCode())
                        .build())
                .collect(Collectors.toList());

        KycStatus kycStatusVal = (customer.getKyc() != null) ? customer.getKyc().getVerificationStatus() : null;

        return CustomerResponse.builder()
                .id(customer.getId())
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .middleName(customer.getMiddleName())
                .lastName(customer.getLastName())
                .gender(customer.getGender())
                .dateOfBirth(customer.getDateOfBirth())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .maritalStatus(customer.getMaritalStatus())
                .occupation(customer.getOccupation())
                .annualIncome(customer.getAnnualIncome())
                .nationality(customer.getNationality())
                .profilePhoto(customer.getProfilePhoto())
                .status(customer.getStatus())
                .addresses(addrList)
                .kycStatus(kycStatusVal)
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    private synchronized String generateNextCustomerId() {
        long count = customerRepository.count();
        return String.format("CUST%06d", count + 1);
    }
}
