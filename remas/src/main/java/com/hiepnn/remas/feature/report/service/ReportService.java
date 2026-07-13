package com.hiepnn.remas.feature.report.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiepnn.remas.common.constant.ContractStatus;
import com.hiepnn.remas.entity.Contract;
import com.hiepnn.remas.entity.Customer;
import com.hiepnn.remas.feature.contract.repository.ContractRepository;
import com.hiepnn.remas.feature.report.model.DashboardReportResponse;
import com.hiepnn.remas.feature.report.model.TopCustomerReportItem;
import com.hiepnn.remas.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public DashboardReportResponse getDashboardReport() {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();

        List<Contract> activeContracts;
        if (isSuperAdmin) {
            activeContracts = contractRepository.findByStatusNot(ContractStatus.CANCELLED);
        } else {
            activeContracts = contractRepository.findByStatusNotAndCreatedBy(ContractStatus.CANCELLED, username);
        }

        long totalCustomers = activeContracts.stream()
                .filter(c -> c.getCustomer() != null && !Boolean.TRUE.equals(c.getCustomer().getIsDeleted()))
                .map(c -> c.getCustomer().getId())
                .distinct()
                .count();

        long totalOrders = activeContracts.size();

        BigDecimal totalRevenue = activeContracts.stream()
                .map(Contract::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageRevenue = totalOrders > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        long totalRentalTime = activeContracts.stream()
                .mapToLong(c -> Math.max(1, ChronoUnit.DAYS.between(c.getStartDate().toLocalDate(), c.getExpectedReturnDate().toLocalDate())))
                .sum();

        BigDecimal averageRentalTime = totalOrders > 0 
                ? BigDecimal.valueOf(totalRentalTime).divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        BigDecimal rentalTimesPerCustomer = totalCustomers > 0 
                ? BigDecimal.valueOf(totalOrders).divide(BigDecimal.valueOf(totalCustomers), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        Map<Customer, List<Contract>> contractsByCustomer = activeContracts.stream()
                .filter(c -> c.getCustomer() != null && !Boolean.TRUE.equals(c.getCustomer().getIsDeleted()))
                .collect(Collectors.groupingBy(Contract::getCustomer));

        List<TopCustomerReportItem> topRevenueCustomers = contractsByCustomer.entrySet().stream()
                .map(entry -> {
                    BigDecimal customerRevenue = entry.getValue().stream()
                            .map(Contract::getFinalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return TopCustomerReportItem.builder()
                            .id(entry.getKey().getId())
                            .name(entry.getKey().getName())
                            .phone(entry.getKey().getPhone())
                            .value(customerRevenue)
                            .build();
                })
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        List<TopCustomerReportItem> topCountCustomers = contractsByCustomer.entrySet().stream()
                .map(entry -> TopCustomerReportItem.builder()
                        .id(entry.getKey().getId())
                        .name(entry.getKey().getName())
                        .phone(entry.getKey().getPhone())
                        .value(BigDecimal.valueOf(entry.getValue().size()))
                        .build())
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        return DashboardReportResponse.builder()
                .totalCustomers(totalCustomers)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageRevenue(averageRevenue)
                .totalRentalTime(totalRentalTime)
                .averageRentalTime(averageRentalTime)
                .rentalTimesPerCustomer(rentalTimesPerCustomer)
                .topRevenueCustomers(topRevenueCustomers)
                .topCountCustomers(topCountCustomers)
                .build();
    }
}
