package com.hiepnn.remas.feature.inventory.category.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiepnn.remas.common.constant.CategoryStatus;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.entity.Category;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.feature.inventory.category.model.CategoryRequest;
import com.hiepnn.remas.feature.inventory.category.repository.CategoryRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  // #region Get all
  @Transactional(readOnly = true)
  public PagingResponse<Category> getAllCategories() {
    List<Category> list = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE, Sort.by(Sort.Direction.DESC, "updatedAt"));
    return PagingResponse.<Category>builder()
        .data(list)
        .total(list.size())
        .page(1)
        .pageSize(list.size())
        .build();
  }
  // #endregion

  //#region Paging & filter
  @Transactional(readOnly = true)
  public PagingResponse<Category> getCategoriesWithFilter(int page, int pageSize, String search, String sortField, String sortOrder) {
    Specification<Category> spec = (root, query, cb) -> {
      Predicate notDeleted = cb.notEqual(root.get("status"), CategoryStatus.DELETED);
      if (search == null || search.trim().isEmpty()) {
        return notDeleted;
      }
      String searchPattern = "%" + search.trim().toLowerCase() + "%";
      Predicate searchPredicate = cb.or(
          cb.like(cb.lower(root.get("name")), searchPattern),
          cb.like(cb.lower(root.get("description")), searchPattern)
      );
      return cb.and(notDeleted, searchPredicate);
    };

    Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
    if (sortField != null && !sortField.trim().isEmpty()) {
      Sort.Direction direction = "descend".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder)
          ? Sort.Direction.DESC
          : Sort.Direction.ASC;
      sort = Sort.by(direction, sortField);
    }

    int pageIndex = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
    Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

    return PagingResponse.<Category>builder()
        .data(categoryPage.getContent())
        .total(categoryPage.getTotalElements())
        .page(page)
        .pageSize(pageSize)
        .build();
  } 
  //#endregion

  // #region Get by id
  @Transactional(readOnly = true)
  public Category getCategoryById(Integer id) {
    return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
  }
  // #endregion

  // #region Create
  @Transactional
  public Category createCategory(CategoryRequest request) {
    if (categoryRepository.existsByName(request.getName())) {
      throw new BadRequestException("Category name already exists!");
    }

    Category category = Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .status(CategoryStatus.INACTIVE)
        .build();

    return categoryRepository.save(category);
  }
  // #endregion

  //#region Update
  @Transactional
  public Category updateCategory(Integer id, CategoryRequest request) {
    Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

    if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
      throw new BadRequestException("Category name already exists!");
    }

    category.setName(request.getName());
    category.setDescription(request.getDescription());

    return categoryRepository.save(category);
  }
  //#endregion

  //#region Delete
  @Transactional
  public void deleteCategory(Integer id) {
    Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

    category.setStatus(CategoryStatus.DELETED);
    categoryRepository.save(category);
  }
  //#endregion
}
