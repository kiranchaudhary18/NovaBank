package com.novabank.backend.service.impl;

import com.novabank.backend.service.CleanupService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementation executing database maintenance and old log pruning.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupServiceImpl implements CleanupService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public int cleanOldLogs() {
        log.info("Starting automated log cleanup maintenance job");
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        int deletedAudits = entityManager.createQuery(
                "DELETE FROM AuditLog a WHERE a.createdAt < :cutoff"
        ).setParameter("cutoff", cutoff)
        .executeUpdate();

        int deletedJobs = entityManager.createQuery(
                "DELETE FROM JobExecutionLog j WHERE j.createdAt < :cutoff"
        ).setParameter("cutoff", cutoff)
        .executeUpdate();

        int total = deletedAudits + deletedJobs;
        log.info("Pruning complete. Deleted: {} audits logs | {} background jobs history entries", deletedAudits, deletedJobs);
        return total;
    }
}
