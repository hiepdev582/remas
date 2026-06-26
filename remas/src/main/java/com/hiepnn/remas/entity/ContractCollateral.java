package com.hiepnn.remas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.hiepnn.remas.common.constant.CollateralType;
import com.hiepnn.remas.common.constant.CollateralItemStatus;

import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "contract_collaterals")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractCollateral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(name = "collateral_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private CollateralType collateralType;

    // Nếu là tiền mặt thì điền số tiền, nếu là vật chất thì điền giá trị ước tính
    // (hoặc 0 nếu là giấy tờ tùy thân)
    // Giá trị quy đổi bằng tiền nếu có
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal value = BigDecimal.ZERO;

    // Mô tả chi tiết: Ví dụ "Xe Honda Wave RSX biển số 29X-XXXX kèm đăng ký", "Tiền mặt 2.000.000đ"
    @Column(name = "asset_description", columnDefinition = "TEXT")
    private String assetDescription;

    @Column(length = 50, nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CollateralItemStatus status = CollateralItemStatus.HOLDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
