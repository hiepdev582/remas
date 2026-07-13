package com.hiepnn.remas.feature.customer.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Integer id;
    private String name;
    private String phone;
    private String identityCard;
    private String driverLicense;
    private Integer trustScore;
    private String gender;
    private LocalDate dob;
    private Integer age;
    private String address;
    private LocalDateTime lastInteractionDate;
    private Long daysSinceLastInteraction;
    private String note;
    private String link;
    private BigDecimal revenue;
    private Long rentalCount;
    private List<CustomerDocumentResponse> documents;
}
