package com.novabank.backend.service;

import com.novabank.backend.dto.AddressRequest;
import com.novabank.backend.dto.AddressResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AddressType;

/**
 * Service interface defining operations related to Customer address management.
 *
 * @author Senior Java Backend Architect
 */
public interface AddressService {

    /**
     * Creates or updates a physical address location for the authenticated customer.
     *
     * @param user current authenticated user
     * @param request address update parameters
     * @return updated AddressResponse DTO
     */
    AddressResponse updateAddress(User user, AddressRequest request);

    /**
     * Fetches a specific address classification location details for the authenticated customer.
     *
     * @param user current authenticated user
     * @param type category classification type
     * @return AddressResponse DTO
     */
    AddressResponse getAddress(User user, AddressType type);
}
