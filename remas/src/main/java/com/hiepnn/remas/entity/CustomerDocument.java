package com.hiepnn.remas.entity;

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
@Table(name = "customer_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // CCCD_FRONT (Mặt trước CCCD), CCCD_BACK (Mặt sau CCCD), DRIVER_LICENSE_FRONT, DRIVER_LICENSE_BACK, OTHER
    @Column(name = "document_type", length = 50, nullable = false)
    private String documentType;

    // Số giấy tờ cụ thể (nếu có)
    @Column(name = "document_number", length = 50)
    private String documentNumber;

    // Đường dẫn URL của ảnh đã upload lên Cloudinary/S3 hoặc Server
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
