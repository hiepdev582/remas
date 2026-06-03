package com.hiepnn.remas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 20, nullable = false, unique = true)
    private String phone;

    // Số CCCD/CMND
    @Column(name = "identity_card", length = 20, unique = true)
    private String identityCard;

    // Số GPLX để tra cứu nhanh
    @Column(name = "driver_license", length = 20, unique = true)
    private String driverLicense;

    // Điểm uy tín của khách hàng
    @Column(name = "trust_score", nullable = false)
    @Builder.Default
    private Integer trustScore = 100;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
