package com.novabank.backend.service.impl;

import com.novabank.backend.dto.AuditLogResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.AuditLog;
import com.novabank.backend.entity.User;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AuditLogRepository;
import com.novabank.backend.service.AuditService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing operations and security audit trails.
 * Runs in a separate transactional context to prevent rolling back audits if main transaction fails.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(
            User user, String action, String module, String status, String description,
            String httpMethod, String api, String ipAddress, String device, String browser, String os
    ) {
        log.info("Auditing action: '{}' in module: '{}' by user: {}", action, module, user != null ? user.getEmail() : "ANONYMOUS");

        AuditLog logEntry = AuditLog.builder()
                .user(user)
                .action(action)
                .module(module)
                .status(status)
                .description(description)
                .httpMethod(httpMethod)
                .api(api)
                .requestId(UUID.randomUUID().toString())
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .device(device != null ? device : "Unknown")
                .browser(browser != null ? browser : "Unknown")
                .operatingSystem(os != null ? os : "Unknown")
                .build();

        auditLogRepository.save(logEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> searchAuditLogs(
            int page, int size, String sortBy, String sortDir,
            String action, String module, String status, UUID userId, String ipAddress
    ) {
        log.debug("Searching administrative audit logs");
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (action != null && !action.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("action")), "%" + action.toLowerCase() + "%"));
            }
            if (module != null && !module.isBlank()) {
                predicates.add(cb.equal(root.get("module"), module));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }
            if (ipAddress != null && !ipAddress.isBlank()) {
                predicates.add(cb.equal(root.get("ipAddress"), ipAddress));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<AuditLog> result = auditLogRepository.findAll(spec, pageable);
        return new PagedResponse<>(result.map(this::convertToAuditLogResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(UUID id) {
        AuditLog logEntry = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log entry not found with ID: " + id));
        return convertToAuditLogResponse(logEntry);
    }

    private AuditLogResponse convertToAuditLogResponse(AuditLog entry) {
        if (entry == null) return null;
        return AuditLogResponse.builder()
                .id(entry.getId())
                .userId(entry.getUser() != null ? entry.getUser().getId() : null)
                .userEmail(entry.getUser() != null ? entry.getUser().getEmail() : "ANONYMOUS")
                .action(entry.getAction())
                .module(entry.getModule())
                .httpMethod(entry.getHttpMethod())
                .api(entry.getApi())
                .requestId(entry.getRequestId())
                .ipAddress(entry.getIpAddress())
                .device(entry.getDevice())
                .browser(entry.getBrowser())
                .operatingSystem(entry.getOperatingSystem())
                .status(entry.getStatus())
                .description(entry.getDescription())
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
