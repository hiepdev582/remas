package com.hiepnn.remas.feature.notification.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hiepnn.remas.entity.Notification;
import com.hiepnn.remas.entity.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findAllByCreatedByOrderByCreatedAtDesc(String createdBy);

    long countByIsReadFalse();

    long countByIsReadFalseAndCreatedBy(String createdBy);

    Optional<Notification> findByTypeAndRelatedId(NotificationType type, Long relatedId);
}
