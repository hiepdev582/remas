package com.hiepnn.remas.feature.report.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hiepnn.remas.common.constant.ContractStatus;
import com.hiepnn.remas.entity.Contract;
import com.hiepnn.remas.entity.ContractDetail;
import com.hiepnn.remas.entity.Customer;
import com.hiepnn.remas.entity.Item;
import com.hiepnn.remas.feature.contract.repository.ContractRepository;
import com.hiepnn.remas.feature.contract.repository.ContractDetailRepository;
import com.hiepnn.remas.feature.report.model.DashboardReportResponse;
import com.hiepnn.remas.feature.report.model.TopCustomerReportItem;
import com.hiepnn.remas.feature.report.model.MonthlyRevenueItem;
import com.hiepnn.remas.feature.report.model.TopItemReportItem;
import com.hiepnn.remas.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ContractRepository contractRepository;
    private final ContractDetailRepository contractDetailRepository;

    @Transactional(readOnly = true)
    public DashboardReportResponse getDashboardReport() {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();

        List<Contract> allUserContracts;
        List<Contract> activeContracts;
        
        if (isSuperAdmin) {
            allUserContracts = contractRepository.findAll();
            activeContracts = contractRepository.findByStatusNot(ContractStatus.CANCELLED);
        } else {
            allUserContracts = contractRepository.findAll().stream()
                .filter(c -> username != null && username.equals(c.getCreatedBy()))
                .toList();
            activeContracts = contractRepository.findByStatusNotAndCreatedBy(ContractStatus.CANCELLED, username);
        }

        // Calculate Cancellation Rate
        long totalContracts = allUserContracts.size();
        long cancelledContracts = allUserContracts.stream()
            .filter(c -> c.getStatus() == ContractStatus.CANCELLED)
            .count();
            
        BigDecimal cancellationRate = totalContracts > 0
            ? BigDecimal.valueOf(cancelledContracts)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalContracts), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Calculate Customer count
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
            .toList();

        List<TopCustomerReportItem> topCountCustomers = contractsByCustomer.entrySet().stream()
            .map(entry -> TopCustomerReportItem.builder()
                .id(entry.getKey().getId())
                .name(entry.getKey().getName())
                .phone(entry.getKey().getPhone())
                .value(BigDecimal.valueOf(entry.getValue().size()))
                .build())
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .toList();

        // 1. Calculate Monthly Revenue
        Map<String, BigDecimal> revenueByMonth = activeContracts.stream()
            .collect(Collectors.groupingBy(
                c -> {
                    LocalDateTime date = c.getStartDate();
                    return String.format("%02d/%d", date.getMonthValue(), date.getYear());
                },
                Collectors.mapping(Contract::getFinalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        List<MonthlyRevenueItem> monthlyRevenue = revenueByMonth.entrySet().stream()
            .map(entry -> MonthlyRevenueItem.builder()
                .month(entry.getKey())
                .revenue(entry.getValue())
                .build())
            .sorted((a, b) -> {
                String[] partsA = a.getMonth().split("/");
                String[] partsB = b.getMonth().split("/");
                int yearCompare = partsA[1].compareTo(partsB[1]);
                if (yearCompare != 0) return yearCompare;
                return partsA[0].compareTo(partsB[0]);
            })
            .toList();

        // 2. Calculate Top Rented Items
        List<Integer> activeContractIds = activeContracts.stream().map(Contract::getId).toList();
        List<ContractDetail> activeDetails = contractDetailRepository.findAll().stream()
            .filter(cd -> cd.getContract() != null && activeContractIds.contains(cd.getContract().getId()))
            .toList();

        Map<Item, Integer> itemRentCounts = activeDetails.stream()
            .filter(cd -> cd.getItem() != null)
            .collect(Collectors.groupingBy(
                ContractDetail::getItem, 
                Collectors.summingInt(ContractDetail::getQuantity)
            ));

        List<TopItemReportItem> topRentedItems = itemRentCounts.entrySet().stream()
            .map(entry -> TopItemReportItem.builder()
                .id(entry.getKey().getId())
                .name(entry.getKey().getName())
                .rentCount(entry.getValue().longValue())
                .build())
            .sorted((a, b) -> b.getRentCount().compareTo(a.getRentCount()))
            .limit(5)
            .toList();

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
            .monthlyRevenue(monthlyRevenue)
            .topRentedItems(topRentedItems)
            .cancellationRate(cancellationRate)
            .build();
    }
}
