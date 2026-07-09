package com.hiepnn.remas.feature.contract.model;

import java.time.LocalDateTime;
import java.util.List;
import com.hiepnn.remas.common.constant.CollateralStatus;
import com.hiepnn.remas.common.constant.ContractStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractRequest {
    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "Expected return date is required")
    private LocalDateTime expectedReturnDate;

    private LocalDateTime actualReturnDate;

    private CollateralStatus collateralStatus = CollateralStatus.NONE;

    private ContractStatus status = ContractStatus.RESERVED;

    @Valid
    private List<ContractDetailRequest> details;

    @Valid
    private List<ContractCollateralRequest> collaterals;

    @Valid
    private List<ContractFeeRequest> fees;
}
