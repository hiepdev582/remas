package com.hiepnn.remas.feature.inventory.item.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiepnn.remas.common.constant.PriceType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemPricingRequest {
    @NotNull(message = "Price type is required")
    private PriceType priceType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Suggested deposit must be greater than or equal to 0")
    private BigDecimal suggestedDeposit = BigDecimal.ZERO;

    @JsonProperty("isActive")
    private Boolean isActive = true;
}
