package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.UserStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.UserRepository;
import com.novabank.backend.service.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing {@link User} entities.
 * Handles database persistence, business rules validation, and access control mappings.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.novabank.backend.service.EventPublisherService eventPublisherService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .filter(user -> user.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching all users: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Soft-deleted users are excluded from standard list requests
        Specification<User> spec = (root, query, cb) -> cb.notEqual(root.get("status"), UserStatus.DELETED);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        Page<UserResponse> responsePage = userPage.map(this::convertToUserResponse);
        return new PagedResponse<>(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> searchUsers(UserFilterRequest filter, int page, int size, String sortBy, String sortDir) {
        log.info("Searching users with filters: {}, page={}, size={}", filter, page, size);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = buildSearchSpecification(filter);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        Page<UserResponse> responsePage = userPage.map(this::convertToUserResponse);
        return new PagedResponse<>(responsePage);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateUserRequest request) {
        log.info("Updating profile details for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Enforce phone uniqueness check
        if (userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
            throw new BadRequestException("Phone number is already in use by another account.");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);
        return convertToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Processing password update for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Validate old password credentials
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password verification failed. Please try again.");
        }

        // Encrypt and save new credentials
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        User savedUser = userRepository.save(user);
        log.info("Password changed successfully for user ID: {}", userId);

        // Publish security update event
        eventPublisherService.publishPasswordChangedEvent(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(UUID userId, UserStatus status) {
        log.info("Updating status to {} for user ID: {}", status, userId);
        User user = userRepository.findById(userId)
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (status == UserStatus.DELETED) {
            throw new BadRequestException("Status cannot be set to DELETED directly. Use the DELETE endpoint.");
        }

        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void softDelete(UUID userId) {
        log.info("Soft deleting user ID: {}", userId);
        User user = userRepository.findById(userId)
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Soft delete user by setting status state
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        log.info("User soft deleted successfully: {}", userId);
    }

    @Override
    public UserResponse convertToUserResponse(User user) {
        if (user == null) {
            return null;
        }

        String roleNameString = (user.getRole() != null && user.getRole().getRoleName() != null)
                ? user.getRole().getRoleName().name()
                : null;

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(roleNameString)
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private Specification<User> buildSearchSpecification(UserFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Enforce soft delete check (ignore deleted accounts unless explicitly requested)
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            } else {
                predicates.add(cb.notEqual(root.get("status"), UserStatus.DELETED));
            }

            // 2. Dynamic criteria predicates
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getPhone() != null && !filter.getPhone().isBlank()) {
                predicates.add(cb.like(root.get("phone"), "%" + filter.getPhone() + "%"));
            }

            if (filter.getRole() != null) {
                Join<User, Role> roleJoin = root.join("role");
                predicates.add(cb.equal(roleJoin.get("roleName"), filter.getRole()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
