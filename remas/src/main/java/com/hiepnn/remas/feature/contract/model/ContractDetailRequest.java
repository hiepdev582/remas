package com.hiepnn.remas.feature.contract.model;

import java.math.BigDecimal;
import com.hiepnn.remas.common.constant.PriceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractDetailRequest {
    @NotNull(message = "Item ID is required")
    private Integer itemId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotNull(message = "Applied price type is required")
    private PriceType appliedPriceType;

    @NotNull(message = "Price applied is required")
    private BigDecimal priceApplied;

    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;

    private String handoverStatus;
}
