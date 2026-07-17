package com.novabank.backend.service;

import com.novabank.backend.dto.ChangePasswordRequest;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.dto.UpdateUserRequest;
import com.novabank.backend.dto.UserFilterRequest;
import com.novabank.backend.dto.UserResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.UserStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

/**
 * Service interface defining operations related to User management.
 * Extends {@link UserDetailsService} for security context bindings.
 *
 * @author Senior Java Backend Architect
 */
public interface UserService extends UserDetailsService {

    /**
     * Retrieves a sanitized user profile representation by email.
     *
     * @param email user login email
     * @return DTO representation
     * @throws com.novabank.backend.exception.ResourceNotFoundException if user is not found
     */
    UserResponse getUserResponseByEmail(String email);

    /**
     * Utility method to map User entity fields to UserResponse DTO.
     *
     * @param user persistence entity
     * @return DTO representation
     */
    UserResponse convertToUserResponse(User user);

    /**
     * Retrieves a sanitized user profile by their UUID.
     *
     * @param id user unique identifier
     * @return user profile DTO
     * @throws com.novabank.backend.exception.ResourceNotFoundException if user is not found
     */
    UserResponse getUserById(UUID id);

    /**
     * Lists all users matching criteria with pagination and sorting support.
     *
     * @param page zero-indexed page number
     * @param size page elements size limit
     * @param sortBy property to sort by
     * @param sortDir sort direction (asc/desc)
     * @return paginated user response
     */
    PagedResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir);

    /**
     * Searches users dynamically based on provided filters, pagination, and sorting.
     *
     * @param filter filters to apply (name, email, phone, status, role)
     * @param page zero-indexed page number
     * @param size page elements size limit
     * @param sortBy property to sort by
     * @param sortDir sort direction (asc/desc)
     * @return paginated user response matching criteria
     */
    PagedResponse<UserResponse> searchUsers(UserFilterRequest filter, int page, int size, String sortBy, String sortDir);

    /**
     * Updates profile details (fullname and phone) of an existing user.
     * Validates uniqueness of phone numbers.
     *
     * @param userId user identifier to update
     * @param request profile update details
     * @return updated user details DTO
     */
    UserResponse updateProfile(UUID userId, UpdateUserRequest request);

    /**
     * Updates user authentication password after validating current credential hashes.
     *
     * @param userId user identifier to update
     * @param request current and new password details
     */
    void changePassword(UUID userId, ChangePasswordRequest request);

    /**
     * Modifies the operational state status of a user (e.g. deactivate or activate).
     *
     * @param userId user identifier to update
     * @param status the new status to apply (ACTIVE, INACTIVE, BLOCKED)
     * @return updated user details DTO
     */
    UserResponse updateStatus(UUID userId, UserStatus status);

    /**
     * Performs a soft delete operation on a user account by updating status to DELETED.
     *
     * @param userId user identifier to delete
     */
    void softDelete(UUID userId);
}
