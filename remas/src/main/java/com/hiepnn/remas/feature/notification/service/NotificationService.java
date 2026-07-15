package com.hiepnn.remas.feature.notification.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hiepnn.remas.entity.Contract;
import com.hiepnn.remas.entity.Customer;
import com.hiepnn.remas.entity.Notification;
import com.hiepnn.remas.entity.NotificationType;
import com.hiepnn.remas.common.constant.ContractStatus;
import com.hiepnn.remas.feature.contract.repository.ContractRepository;
import com.hiepnn.remas.feature.customer.repository.CustomerRepository;
import com.hiepnn.remas.feature.notification.repository.NotificationRepository;
import com.hiepnn.remas.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final SseEmittersService sseEmittersService;

    //#region Get All
    public List<Notification> getAllNotifications() {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (isSuperAdmin) {
            return notificationRepository.findAllByOrderByCreatedAtDesc();
        } else if (username != null) {
            return notificationRepository.findAllByCreatedByOrderByCreatedAtDesc(username);
        }
        return List.of();
    }
    //#endregion

    //#region Get unread
    public long getUnreadCount() {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (isSuperAdmin) {
            return notificationRepository.countByIsReadFalse();
        } else if (username != null) {
            return notificationRepository.countByIsReadFalseAndCreatedBy(username);
        }
        return 0L;
    }

    private long getUnreadCountForUser(String username) {
        if (username == null) return 0L;
        // Check if user is super admin - we don't have direct check here but we can assume role based check is outside,
        // or we check if user has super admin role in current context if they are requesting.
        // But for broadcast, we can query by createdBy since it's targeted.
        return notificationRepository.countByIsReadFalseAndCreatedBy(username);
    }
    //#endregion

    //#region Mark as read
    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        
        // Broadcast the updated unread count to this specific user
        if (updated.getCreatedBy() != null) {
            sseEmittersService.sendToUser(updated.getCreatedBy(), "unread_count", getUnreadCountForUser(updated.getCreatedBy()));
        }
        return updated;
    }

    @Transactional
    public void markAllAsRead() {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        List<Notification> notifications;

        if (isSuperAdmin) {
            notifications = notificationRepository.findAll();
        } else if (username != null) {
            notifications = notificationRepository.findAllByCreatedByOrderByCreatedAtDesc(username);
        } else {
            return;
        }

        for (Notification notification : notifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(notifications);

        if (isSuperAdmin) {
            sseEmittersService.sendToAll("unread_count", 0L);
        } else if (username != null) {
            sseEmittersService.sendToUser(username, "unread_count", 0L);
        }
    }
    //#endregion

    //#region Reminder
    @Transactional
    public void checkAndGenerateReminders() {
        log.info("Checking and generating reminders...");
        LocalDateTime now = LocalDateTime.now();

        // 1. Check upcoming handovers (Reserved contracts starting in <= 24 hours)
        List<Contract> reservedContracts = contractRepository.findAll().stream()
            .filter(c -> c.getStatus() == ContractStatus.RESERVED)
            .toList();

        for (Contract contract : reservedContracts) {
            long hoursToStart = ChronoUnit.HOURS.between(now, contract.getStartDate());
            if (hoursToStart >= 0 && hoursToStart <= 24) {
                // Check if notification already exists
                if (notificationRepository.findByTypeAndRelatedId(NotificationType.UPCOMING_HANDOVER, contract.getId().longValue()).isEmpty()) {
                    String title = "Upcoming Vehicle Handover";
                    String customerName = contract.getCustomer() != null ? contract.getCustomer().getName() : "Customer";
                    String content = String.format("Contract #%d for %s is scheduled for handover in %d hours (at %s).",
                        contract.getId(), customerName, hoursToStart, contract.getStartDate().toString());

                    Notification notification = Notification.builder()
                        .type(NotificationType.UPCOMING_HANDOVER)
                        .title(title)
                        .content(content)
                        .relatedId(contract.getId().longValue())
                        .createdBy(contract.getCreatedBy())
                        .build();

                    notificationRepository.save(notification);
                    if (contract.getCreatedBy() != null) {
                        sseEmittersService.sendToUser(contract.getCreatedBy(), "notification", notification);
                        sseEmittersService.sendToUser(contract.getCreatedBy(), "unread_count", getUnreadCountForUser(contract.getCreatedBy()));
                    }
                }
            }
        }

        // 2. Check upcoming returns (Active contracts ending in <= 24 hours)
        List<Contract> activeContracts = contractRepository.findAll().stream()
            .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
            .toList();

        for (Contract contract : activeContracts) {
            long hoursToReturn = ChronoUnit.HOURS.between(now, contract.getExpectedReturnDate());
            if (hoursToReturn >= 0 && hoursToReturn <= 24) {
                // Check if notification already exists
                if (notificationRepository.findByTypeAndRelatedId(NotificationType.UPCOMING_RETURN, contract.getId().longValue()).isEmpty()) {
                    String title = "Upcoming Vehicle Return";
                    String customerName = contract.getCustomer() != null ? contract.getCustomer().getName() : "Customer";
                    String content = String.format("Contract #%d for %s is scheduled for return in %d hours (at %s).",
                        contract.getId(), customerName, hoursToReturn, contract.getExpectedReturnDate().toString());

                    Notification notification = Notification.builder()
                        .type(NotificationType.UPCOMING_RETURN)
                        .title(title)
                        .content(content)
                        .relatedId(contract.getId().longValue())
                        .createdBy(contract.getCreatedBy())
                        .build();

                    notificationRepository.save(notification);
                    if (contract.getCreatedBy() != null) {
                        sseEmittersService.sendToUser(contract.getCreatedBy(), "notification", notification);
                        sseEmittersService.sendToUser(contract.getCreatedBy(), "unread_count", getUnreadCountForUser(contract.getCreatedBy()));
                    }
                }
            }
        }

        // 3. Check inactive customers (No interaction for > 30 days)
        List<Customer> customers = customerRepository.findAll().stream()
            .filter(c -> !c.getIsDeleted())
            .toList();

        for (Customer customer : customers) {
            LocalDateTime lastInteraction = customer.getLastInteractionDate();
            if (lastInteraction == null) {
                lastInteraction = customer.getCreatedAt();
            }

            long daysSinceInteraction = ChronoUnit.DAYS.between(lastInteraction, now);
            if (daysSinceInteraction >= 30) {
                // Only alert once per 30-day window to avoid flooding
                boolean alreadyNotifiedRecently = notificationRepository.findByTypeAndRelatedId(NotificationType.INACTIVE_CUSTOMER, customer.getId().longValue())
                    .map(n -> ChronoUnit.DAYS.between(n.getCreatedAt(), now) < 30)
                    .orElse(false);

                if (!alreadyNotifiedRecently) {
                    String title = "Inactive Customer Retention";
                    String content = String.format("Customer %s has not had any interaction for %d days. Consider contacting them with promotional offers.",
                        customer.getName(), daysSinceInteraction);

                    Notification notification = Notification.builder()
                        .type(NotificationType.INACTIVE_CUSTOMER)
                        .title(title)
                        .content(content)
                        .relatedId(customer.getId().longValue())
                        .createdBy(customer.getCreatedBy())
                        .build();

                    notificationRepository.save(notification);
                    if (customer.getCreatedBy() != null) {
                        sseEmittersService.sendToUser(customer.getCreatedBy(), "notification", notification);
                        sseEmittersService.sendToUser(customer.getCreatedBy(), "unread_count", getUnreadCountForUser(customer.getCreatedBy()));
                    }
                }
            }
        }
    }
    //#endregion
}
