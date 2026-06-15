package com.hiepnn.remas.feature.auth.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hiepnn.remas.feature.auth.repository.InvalidatedTokenRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TokenCleanupScheduler {
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    // cron: * * * * * * - seconds minutes hours day_of_the_month month day_of_the_week
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanExpiredTokens() {
        invalidatedTokenRepository.deleteByExpiryTimeLessThan(LocalDateTime.now());
    }
}
