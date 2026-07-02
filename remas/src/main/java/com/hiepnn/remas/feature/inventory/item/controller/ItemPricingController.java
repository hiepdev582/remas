package com.hiepnn.remas.feature.inventory.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.hiepnn.remas.feature.inventory.item.model.ItemPricingRequest;
import com.hiepnn.remas.feature.inventory.item.model.ItemPricingResponse;
import com.hiepnn.remas.feature.inventory.item.service.ItemPricingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/item/{itemId}/pricing")
@Tag(name = "Inventory Item Pricing", description = "Endpoints API quản lý cấu hình giá đồ cho thuê")
@RequiredArgsConstructor
public class ItemPricingController {
    private final ItemPricingService itemPricingService;

    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình giá của đồ cho thuê", description = "Lấy danh sách cấu hình giá theo ID đồ cho thuê")
    public ResponseEntity<List<ItemPricingResponse>> getPricings(@PathVariable Integer itemId) {
        return ResponseEntity.ok(itemPricingService.getPricingsByItemId(itemId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Thêm cấu hình giá mới cho đồ cho thuê", description = "Thêm cấu hình giá mới")
    public ResponseEntity<ItemPricingResponse> addPricing(
            @PathVariable Integer itemId,
            @Valid @RequestBody ItemPricingRequest request) {
        ItemPricingResponse response = itemPricingService.addPricing(itemId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{pricingId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Cập nhật cấu hình giá của đồ cho thuê", description = "Cập nhật cấu hình giá theo ID")
    public ResponseEntity<ItemPricingResponse> updatePricing(
            @PathVariable Integer itemId,
            @PathVariable Integer pricingId,
            @Valid @RequestBody ItemPricingRequest request) {
        ItemPricingResponse response = itemPricingService.updatePricing(itemId, pricingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{pricingId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Xóa cấu hình giá của đồ cho thuê", description = "Xóa cấu hình giá theo ID")
    public ResponseEntity<Void> deletePricing(
            @PathVariable Integer itemId,
            @PathVariable Integer pricingId) {
        itemPricingService.deletePricing(itemId, pricingId);
        return ResponseEntity.noContent().build();
    }
}
