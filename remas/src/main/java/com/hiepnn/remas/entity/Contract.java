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

import com.hiepnn.remas.common.constant.CollateralStatus;
import com.hiepnn.remas.common.constant.ContractStatus;

import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "contracts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Ngày giờ dự kiến khách sẽ qua lấy đồ
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // Thời gian phải trả dự kiến
    @Column(name = "expected_return_date", nullable = false)
    private LocalDateTime expectedReturnDate;

    // Thời gian thực tế trả đồ
    @Column(name = "actual_return_date")
    private LocalDateTime actualReturnDate;

    // Tổng tiền thuê đồ thuần túy
    @Column(name = "total_item_price", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalItemPrice = BigDecimal.ZERO;

    // Tổng các loại chi phí phát sinh cộng thêm
    @Column(name = "total_fee_price", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalFeePrice = BigDecimal.ZERO;

    // Số tiền cuối cùng khách phải trả = total_item_price + total_fee_price
    @Column(name = "final_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "collateral_status", length = 50, nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CollateralStatus collateralStatus = CollateralStatus.NONE;

    @Column(length = 50, nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ContractStatus status = ContractStatus.RESERVED;

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
