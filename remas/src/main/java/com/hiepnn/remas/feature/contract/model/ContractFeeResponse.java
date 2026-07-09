package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import com.hiepnn.remas.common.constant.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractFeeResponse {
    private Integer id;
    private FeeType feeType;
    private BigDecimal amount;
    private String pickupLocation;
    private String returnLocation;
    private String note;
}
