package com.hiepnn.remas.entity;

import java.math.BigDecimal;

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
    private String appliedPriceType;

    // Giá tiền tính trên 1 đơn vị thời gian tại lúc ký
    @Column(name = "price_applied", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceApplied;

    // Thành tiền tạm tính ban đầu cho món đồ này
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Thêm trường lưu tình trạng đồ trước và sau khi bàn giao (Dạng JSON)
    @Column(name = "handover_status", columnDefinition = "LONGTEXT")
    private String handoverStatus;
}
