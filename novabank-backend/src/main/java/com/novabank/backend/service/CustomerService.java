package com.novabank.backend.service;

import com.novabank.backend.dto.CustomerRequest;
import com.novabank.backend.dto.CustomerResponse;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface defining customer profile management operations.
 *
 * @author Senior Java Backend Architect
 */
public interface CustomerService {

    /**
     * Creates a new customer profile for the currently logged in User.
     * Enforces unique constraint validations on email and phone numbers.
     *
     * @param user current authenticated user
     * @param request profile creation parameters
     * @return CustomerResponse profile details DTO
     */
    CustomerResponse createProfile(User user, CustomerRequest request);

    /**
     * Updates an existing customer profile.
     * Enforces unique constraint validations on email and phone numbers.
     *
     * @param user current authenticated user
     * @param request profile update parameters
     * @return updated CustomerResponse details DTO
     */
    CustomerResponse updateProfile(User user, CustomerRequest request);

    /**
     * Retrieves the customer profile corresponding to the currently authenticated user.
     *
     * @param user current authenticated user
     * @return CustomerResponse profile details DTO
     * @throws com.novabank.backend.exception.ResourceNotFoundException if profile does not exist
     */
    CustomerResponse getMyProfile(User user);

    /**
     * Retrieves a customer profile by its UUID.
     *
     * @param id customer UUID
     * @return CustomerResponse profile details DTO
     */
    CustomerResponse getCustomerById(UUID id);

    /**
     * Uploads and updates the profile photo for the authenticated customer.
     *
     * @param user current authenticated user
     * @param file photo multipart payload
     * @return updated CustomerResponse details DTO
     */
    CustomerResponse uploadProfilePhoto(User user, MultipartFile file);

    /**
     * Searches a customer profile by specific criteria fields:
     * "customerId", "phone", "email", "pan", or "aadhaar".
     *
     * @param field criteria parameter key
     * @param query criteria value query
     * @return CustomerResponse matching profile details DTO
     */
    CustomerResponse searchCustomer(String field, String query);

    /**
     * Helper to map Customer entity details to CustomerResponse DTO.
     *
     * @param customer Customer persistence entity
     * @return CustomerResponse DTO representation
     */
    CustomerResponse convertToCustomerResponse(Customer customer);
}
