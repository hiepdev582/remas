package com.hiepnn.remas.feature.auth.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.hiepnn.remas.entity.InvalidatedToken;

import jakarta.transaction.Transactional;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    @Modifying
    @Transactional
    void deleteByExpiryTimeLessThan(LocalDateTime expiryTime);
}
