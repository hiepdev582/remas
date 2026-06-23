package com.hiepnn.remas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hiepnn.remas.common.constant.PriceType;

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

@Entity
@Table(name = "contract_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    // Lưu lại khách thuê theo kiểu gì: HOURLY, WEEKLY, HOLIDAY...
    @Column(name = "applied_price_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceType appliedPriceType;    

    // Giá tiền tính trên 1 đơn vị thời gian tại lúc ký
    @Column(name = "price_applied", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceApplied;

    // Thành tiền tạm tính ban đầu cho món đồ này
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Thêm trường lưu tình trạng đồ trước và sau khi bàn giao (Dạng JSON)
    @Column(name = "handover_status", columnDefinition = "LONGTEXT")
    private String handoverStatus;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
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
