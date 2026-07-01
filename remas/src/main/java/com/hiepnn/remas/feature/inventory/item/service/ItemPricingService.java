package com.hiepnn.remas.feature.inventory.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.entity.Item;
import com.hiepnn.remas.entity.ItemPricing;
import com.hiepnn.remas.feature.inventory.item.model.ItemPricingRequest;
import com.hiepnn.remas.feature.inventory.item.model.ItemPricingResponse;
import com.hiepnn.remas.feature.inventory.item.repository.ItemPricingRepository;
import com.hiepnn.remas.feature.inventory.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemPricingService {
    private final ItemRepository itemRepository;
    private final ItemPricingRepository itemPricingRepository;

    private ItemPricingResponse mapToResponse(ItemPricing pricing) {
        return ItemPricingResponse.builder()
                .id(pricing.getId())
                .itemId(pricing.getItem().getId())
                .priceType(pricing.getPriceType())
                .price(pricing.getPrice())
                .suggestedDeposit(pricing.getSuggestedDeposit())
                .isActive(pricing.getIsActive())
                .build();
    }

    //#region All by item
    @Transactional(readOnly = true)
    public List<ItemPricingResponse> getPricingsByItemId(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found!");
        }
        return itemPricingRepository.findByItemId(itemId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    //#endregion

    //#region Create
    @Transactional
    public ItemPricingResponse addPricing(Integer itemId, ItemPricingRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found!"));

        if (itemPricingRepository.existsByItemIdAndPriceType(itemId, request.getPriceType())) {
            throw new BadRequestException("Pricing type " + request.getPriceType() + " already exists for this item!");
        }

        ItemPricing pricing = ItemPricing.builder()
                .item(item)
                .priceType(request.getPriceType())
                .price(request.getPrice())
                .suggestedDeposit(request.getSuggestedDeposit())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        ItemPricing saved = itemPricingRepository.save(pricing);
        return mapToResponse(saved);
    }
    //#endregion

    //#region Update
    @Transactional
    public ItemPricingResponse updatePricing(Integer itemId, Integer pricingId, ItemPricingRequest request) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found!");
        }

        ItemPricing pricing = itemPricingRepository.findByIdAndItemId(pricingId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing configuration not found for this item!"));

        if (itemPricingRepository.existsByItemIdAndPriceTypeAndIdNot(itemId, request.getPriceType(), pricingId)) {
            throw new BadRequestException("Pricing type " + request.getPriceType() + " already exists for another record on this item!");
        }

        pricing.setPriceType(request.getPriceType());
        pricing.setPrice(request.getPrice());
        pricing.setSuggestedDeposit(request.getSuggestedDeposit());
        if (request.getIsActive() != null) {
            pricing.setIsActive(request.getIsActive());
        }

        ItemPricing saved = itemPricingRepository.save(pricing);
        return mapToResponse(saved);
    }
    //#endregion

    //#region Delete
    @Transactional
    public void deletePricing(Integer itemId, Integer pricingId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found!");
        }

        ItemPricing pricing = itemPricingRepository.findByIdAndItemId(pricingId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing configuration not found for this item!"));

        itemPricingRepository.delete(pricing);
    }
    //#endregion
}
