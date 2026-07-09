package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import com.hiepnn.remas.common.constant.FeeType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractFeeRequest {
    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String pickupLocation;

    private String returnLocation;

    private String note;
}
