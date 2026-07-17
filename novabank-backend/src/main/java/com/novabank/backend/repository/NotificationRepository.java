package com.novabank.backend.repository;

import com.novabank.backend.entity.Notification;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Notification} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based paginated search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {

    /**
     * Finds notifications by delivery status using pagination.
     */
    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

    /**
     * Lists notifications associated with a user, filtered by read status.
     *
     * @param user target user profile
     * @param isRead read status flag
     * @return list of notifications
     */
    List<Notification> findByUserAndIsRead(User user, boolean isRead);

    /**
     * Counts unread notifications for a user.
     *
     * @param user target user profile
     * @param isRead read status flag (usually false)
     * @return count of unread items
     */
    long countByUserAndIsRead(User user, boolean isRead);
}
