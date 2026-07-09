package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.hiepnn.remas.common.constant.CollateralStatus;
import com.hiepnn.remas.common.constant.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private Integer id;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime startDate;
    private LocalDateTime expectedReturnDate;
    private LocalDateTime actualReturnDate;
    private BigDecimal totalItemPrice;
    private BigDecimal totalFeePrice;
    private BigDecimal finalAmount;
    private CollateralStatus collateralStatus;
    private ContractStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    private List<ContractDetailResponse> details;
    private List<ContractCollateralResponse> collaterals;
    private List<ContractFeeResponse> fees;
}
