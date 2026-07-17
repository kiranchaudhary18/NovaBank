package com.novabank.backend.service.impl;

import com.novabank.backend.dto.AddressRequest;
import com.novabank.backend.dto.AddressResponse;
import com.novabank.backend.entity.Address;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AddressType;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AddressRepository;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing customer {@link Address} updates.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResponse updateAddress(User user, AddressRequest request) {
        log.info("Updating address of type {} for user: {}", request.getAddressType(), user.getEmail());
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        Address address = addressRepository.findByCustomerAndAddressType(customer, request.getAddressType())
                .orElseGet(() -> Address.builder()
                        .customer(customer)
                        .addressType(request.getAddressType())
                        .build());

        address.setHouseNumber(request.getHouseNumber());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        Address savedAddress = addressRepository.save(address);
        log.info("Address updated successfully with ID: {}", savedAddress.getId());
        return convertToAddressResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddress(User user, AddressType type) {
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        Address address = addressRepository.findByCustomerAndAddressType(customer, type)
                .orElseThrow(() -> new ResourceNotFoundException("Address profile not found for type: " + type));

        return convertToAddressResponse(address);
    }

    private AddressResponse convertToAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .houseNumber(address.getHouseNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .district(address.getDistrict())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
