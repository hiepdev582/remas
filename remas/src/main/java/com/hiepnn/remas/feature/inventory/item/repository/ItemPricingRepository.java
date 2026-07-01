package com.hiepnn.remas.feature.inventory.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.hiepnn.remas.entity.ItemPricing;
import com.hiepnn.remas.common.constant.PriceType;

public interface ItemPricingRepository extends JpaRepository<ItemPricing, Integer> {
    List<ItemPricing> findByItemId(Integer itemId);

    Optional<ItemPricing> findByIdAndItemId(Integer id, Integer itemId);

    boolean existsByItemIdAndPriceTypeAndIdNot(Integer itemId, PriceType priceType, Integer id);

    boolean existsByItemIdAndPriceType(Integer itemId, PriceType priceType);
}
