package com.hiepnn.remas.feature.report.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerReportItem {
    private Integer id;
    private String name;
    private String phone;
    private BigDecimal value;
}
