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
import com.hiepnn.remas.common.constant.RoleName;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.entity.Category;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.feature.inventory.category.model.CategoryRequest;
import com.hiepnn.remas.feature.inventory.category.repository.CategoryRepository;
import com.hiepnn.remas.feature.auth.repository.UserRepository;
import com.hiepnn.remas.feature.auth.repository.UserRoleRepository;
import com.hiepnn.remas.entity.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;

  private boolean isSuperAdminUser(String username) {
    if (username == null) {
      return false;
    }
    return userRepository.findByUsername(username)
        .map(user -> {
          List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
          return userRoles.stream()
              .anyMatch(ur -> ur.getRole().getName().getValue().equals(RoleName.SUPER_ADMIN.getValue()));
        })
        .orElse(false);
  }

  private void populateCreatedBySuperAdmin(Category category) {
    if (category.getCreatedBy() != null) {
      category.setCreatedBySuperAdmin(isSuperAdminUser(category.getCreatedBy()));
    } else {
      category.setCreatedBySuperAdmin(false);
    }
  }

  // #region Get all
  @Transactional(readOnly = true)
  public PagingResponse<Category> getAllCategories() {
    List<Category> list = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE,
        Sort.by(Sort.Direction.DESC, "updatedAt"));
    list.forEach(this::populateCreatedBySuperAdmin);
    return PagingResponse.<Category>builder()
        .data(list)
        .total(list.size())
        .page(1)
        .pageSize(list.size())
        .build();
  }
  // #endregion

  // #region Paging & filter
  @Transactional(readOnly = true)
  public PagingResponse<Category> getCategoriesWithFilter(int page, int pageSize, String search, String sortField,
      String sortOrder, List<CategoryStatus> status) {
    Specification<Category> spec = (root, query, cb) -> {
      Predicate p = cb.conjunction();

      if (status != null && !status.isEmpty()) {
        p = cb.and(p, root.get("status").in(status));
      } else {
        p = cb.and(p, cb.notEqual(root.get("status"), CategoryStatus.DELETED));
      }

      if (search != null && !search.trim().isEmpty()) {
        String searchPattern = "%" + search.trim().toLowerCase() + "%";
        Predicate searchPredicate = cb.or(
            cb.like(cb.lower(root.get("name")), searchPattern),
            cb.like(cb.lower(root.get("description")), searchPattern));
        p = cb.and(p, searchPredicate);
      }
      return p;
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
    categoryPage.getContent().forEach(this::populateCreatedBySuperAdmin);

    return PagingResponse.<Category>builder()
        .data(categoryPage.getContent())
        .total(categoryPage.getTotalElements())
        .page(page)
        .pageSize(pageSize)
        .build();
  }
  // #endregion

  // #region Get by id
  @Transactional(readOnly = true)
  public Category getCategoryById(Integer id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
    if (category.getStatus() == CategoryStatus.DELETED) {
      throw new ResourceNotFoundException("Category not found!");
    }
    populateCreatedBySuperAdmin(category);
    return category;
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

    Category saved = categoryRepository.save(category);
    populateCreatedBySuperAdmin(saved);
    return saved;
  }
  // #endregion

  // #region Update
  @Transactional
  public Category updateCategory(Integer id, CategoryRequest request) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
    if (category.getStatus() == CategoryStatus.DELETED) {
      throw new ResourceNotFoundException("Category not found!");
    }

    // Check if category is created by super admin and current user is not super admin
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isCurrentSuperAdmin = authentication != null && authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals(RoleName.SUPER_ADMIN.getValue()));
    
    if (!isCurrentSuperAdmin && category.getCreatedBy() != null) {
      boolean isCreatorSuperAdmin = isSuperAdminUser(category.getCreatedBy());
      if (isCreatorSuperAdmin) {
        throw new BadRequestException("You do not have permission to edit this category because it was created by a Super Admin.");
      }
    }

    if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
      throw new BadRequestException("Category name already exists!");
    }

    category.setName(request.getName());
    category.setDescription(request.getDescription());

    Category saved = categoryRepository.save(category);
    populateCreatedBySuperAdmin(saved);
    return saved;
  }
  // #endregion

  // #region Update
  @Transactional
  public Category updateCategoryStatus(Integer id, CategoryStatus status) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
    if (category.getStatus() == CategoryStatus.DELETED) {
      throw new ResourceNotFoundException("Category not found!");
    }

    category.setStatus(status);

    Category saved = categoryRepository.save(category);
    populateCreatedBySuperAdmin(saved);
    return saved;
  }
  // #endregion

  // #region Delete
  @Transactional
  public void deleteCategory(Integer id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

    category.setStatus(CategoryStatus.DELETED);
    categoryRepository.save(category);
  }
  // #endregion
}
