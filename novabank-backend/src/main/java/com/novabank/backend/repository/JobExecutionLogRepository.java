package com.novabank.backend.repository;

import com.novabank.backend.entity.JobExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link JobExecutionLog} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based search pagination.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, UUID>, JpaSpecificationExecutor<JobExecutionLog> {
}
