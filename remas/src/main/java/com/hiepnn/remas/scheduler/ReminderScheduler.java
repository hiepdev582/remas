package com.hiepnn.remas.scheduler;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.hiepnn.remas.feature.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final NotificationService notificationService;

    // Run every hour to scan contracts and generate reminders
    @Scheduled(cron = "0 0 * * * *")
    public void runReminderCheck() {
        try {
            notificationService.checkAndGenerateReminders();
        } catch (Exception e) {
            log.error("Error occurred during scheduled reminder check", e);
        }
    }

    // Also run immediately on application startup
    @EventListener(ApplicationReadyEvent.class)
    public void initCheck() {
        log.info("Application ready. Running initial reminder check...");
        try {
            notificationService.checkAndGenerateReminders();
        } catch (Exception e) {
            log.error("Error occurred during initial startup reminder check", e);
        }
    }
}
