package com.hiepnn.remas.feature.inventory.item.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiepnn.remas.common.constant.PriceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPricingResponse {
    private Integer id;
    private Integer itemId;
    private PriceType priceType;
    private BigDecimal price;
    private BigDecimal suggestedDeposit;
    @JsonProperty("isActive")
    private Boolean isActive;
}
