package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import com.hiepnn.remas.common.constant.PriceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailResponse {
    private Integer id;
    private Integer itemId;
    private String itemName;
    private Integer quantity;
    private PriceType appliedPriceType;
    private BigDecimal priceApplied;
    private BigDecimal subtotal;
    private String handoverStatus;
}
