package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import com.hiepnn.remas.common.constant.CollateralType;
import com.hiepnn.remas.common.constant.CollateralItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractCollateralResponse {
    private Integer id;
    private CollateralType collateralType;
    private BigDecimal value;
    private String assetDescription;
    private CollateralItemStatus status;
}
