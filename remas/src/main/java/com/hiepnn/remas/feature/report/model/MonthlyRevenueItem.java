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
public class MonthlyRevenueItem {
    private String month; // format "MM/YYYY"
    private BigDecimal revenue;
}
