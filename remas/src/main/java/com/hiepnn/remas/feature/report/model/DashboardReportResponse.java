package com.hiepnn.remas.feature.report.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReportResponse {
    private Long totalCustomers;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;
    private Long totalRentalTime;
    private BigDecimal averageRentalTime;
    private BigDecimal rentalTimesPerCustomer;
    private List<TopCustomerReportItem> topRevenueCustomers;
    private List<TopCustomerReportItem> topCountCustomers;
}
