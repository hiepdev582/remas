package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import com.hiepnn.remas.common.constant.CollateralType;
import com.hiepnn.remas.common.constant.CollateralItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractCollateralRequest {
    @NotNull(message = "Collateral type is required")
    private CollateralType collateralType;

    private BigDecimal value = BigDecimal.ZERO;

    private String assetDescription;

    private CollateralItemStatus status = CollateralItemStatus.HOLDING;
}
