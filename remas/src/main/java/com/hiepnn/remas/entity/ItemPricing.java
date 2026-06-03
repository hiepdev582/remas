package com.hiepnn.remas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_pricings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; 

    // HOURLY, DAILY, WEEKLY, MONTHLY, WEEKEND, HOLIDAY
    @Column(name = "price_type", length = 50, nullable = false)
    private String priceType;
    
    // Số tiền áp dụng cho loại hình đó
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;   

    // Giá tiền cọc gợi ý riêng cho gói thuê này
    @Column(name = "suggested_deposit", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal suggestedDeposit = BigDecimal.ZERO;   
    
    // Bật/Tắt cấu hình giá này
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; 

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
