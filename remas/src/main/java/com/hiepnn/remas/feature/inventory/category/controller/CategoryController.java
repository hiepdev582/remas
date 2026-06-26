package com.hiepnn.remas.feature.inventory.category.controller;

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

import com.hiepnn.remas.common.constant.CategoryStatus;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.entity.Category;
import com.hiepnn.remas.feature.inventory.category.model.CategoryRequest;
import com.hiepnn.remas.feature.inventory.category.model.CategoryStatusRequest;
import com.hiepnn.remas.feature.inventory.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/category")
@Tag(name = "Inventory Category", description = "Endpoints API xử lý CRUD danh mục")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  @Operation(summary = "Danh sách tất cả danh mục", description = "Lấy tất cả danh mục")
  public ResponseEntity<PagingResponse<Category>> getAll() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @GetMapping("/list")
  @Operation(summary = "Danh sách danh mục theo bộ lọc", description = "Lấy danh mục theo bộ lọc")
  public ResponseEntity<PagingResponse<Category>> getList(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false) String sortOrder,
      @RequestParam(required = false) List<CategoryStatus> status) {
    return ResponseEntity.ok(categoryService.getCategoriesWithFilter(page, pageSize, search, sortField, sortOrder, status));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết danh mục", description = "Lấy danh mục theo id")
  public ResponseEntity<Category> getById(@PathVariable Integer id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
  @Operation(summary = "Tạo danh mục", description = "Thêm mới danh mục")
  public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequest request) {
    Category response = categoryService.createCategory(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
  @Operation(summary = "Cập nhật danh mục", description = "Cập nhật danh mục")
  public ResponseEntity<Category> update(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request) {
    return ResponseEntity.ok(categoryService.updateCategory(id, request));
  }

  @PutMapping("/update-status/{id}")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  @Operation(summary = "Cập nhật trạng thái danh mục", description = "Cập nhật trạng thái danh mục")
  public ResponseEntity<Category> updateStatus(@PathVariable Integer id, @Valid @RequestBody CategoryStatusRequest request) {
    return ResponseEntity.ok(categoryService.updateCategoryStatus(id, CategoryStatus.valueOf(request.getStatus())));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  @Operation(summary = "Xóa danh mục", description = "Xóa danh mục")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
