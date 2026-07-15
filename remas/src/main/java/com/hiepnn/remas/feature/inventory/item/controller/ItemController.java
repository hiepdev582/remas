package com.hiepnn.remas.feature.inventory.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.hiepnn.remas.common.constant.ItemStatus;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.feature.inventory.item.model.ItemRequest;
import com.hiepnn.remas.feature.inventory.item.model.ItemResponse;
import com.hiepnn.remas.feature.inventory.item.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/item")
@Tag(name = "Inventory Item", description = "Endpoints API xử lý CRUD đồ cho thuê")
public class ItemController {
  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping
  @Operation(summary = "Danh sách tất cả đồ cho thuê", description = "Lấy tất cả đồ cho thuê")
  public ResponseEntity<PagingResponse<ItemResponse>> getAll() {
    return ResponseEntity.ok(itemService.getAllItems());
  }

  @GetMapping("/by-category/{categoryId}")
  @Operation(summary = "Danh sách tất cả đồ cho thuê theo danh mục", description = "Lấy tất cả đồ cho thuê theo danh mục")
  public ResponseEntity<PagingResponse<ItemResponse>> getAllByCategory(@PathVariable Integer categoryId) {
    return ResponseEntity.ok(itemService.getAllItemsByCategory(categoryId));
  }

  @GetMapping("/list")
  @Operation(summary = "Danh sách đồ cho thuê theo bộ lọc", description = "Lấy đồ cho thuê theo bộ lọc")
  public ResponseEntity<PagingResponse<ItemResponse>> getList(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false) String sortOrder,
      @RequestParam(required = false) List<ItemStatus> status,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Integer ownerId) {
    return ResponseEntity.ok(itemService.getItemsWithFilter(page, pageSize, search, sortField, sortOrder, status, categoryId, ownerId));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết đồ cho thuê", description = "Lấy đồ cho thuê theo id")
  public ResponseEntity<ItemResponse> getById(@PathVariable Integer id) {
    return ResponseEntity.ok(itemService.getItemById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
  @Operation(summary = "Tạo đồ cho thuê", description = "Thêm mới đồ cho thuê")
  public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest request) {
    ItemResponse response = itemService.createItem(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
  @Operation(summary = "Cập nhật đồ cho thuê", description = "Cập nhật đồ cho thuê")
  public ResponseEntity<ItemResponse> update(@PathVariable Integer id, @Valid @RequestBody ItemRequest request) {
    return ResponseEntity.ok(itemService.updateItem(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
  @Operation(summary = "Xóa đồ cho thuê", description = "Xóa đồ cho thuê")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    itemService.deleteItem(id);
    return ResponseEntity.noContent().build();
  }
}
