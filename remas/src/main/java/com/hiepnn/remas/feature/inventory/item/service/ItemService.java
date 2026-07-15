package com.hiepnn.remas.feature.inventory.item.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiepnn.remas.common.constant.ItemStatus;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.entity.Category;
import com.hiepnn.remas.entity.Item;
import com.hiepnn.remas.entity.User;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.feature.auth.repository.UserRepository;
import com.hiepnn.remas.feature.inventory.category.repository.CategoryRepository;
import com.hiepnn.remas.feature.inventory.item.model.ItemRequest;
import com.hiepnn.remas.feature.inventory.item.model.ItemResponse;
import com.hiepnn.remas.feature.inventory.item.repository.ItemRepository;
import com.hiepnn.remas.util.SecurityUtils;

import com.hiepnn.remas.entity.ItemImage;
import com.hiepnn.remas.feature.inventory.item.model.ItemImageRequest;
import com.hiepnn.remas.feature.inventory.item.model.ItemImageResponse;
import com.hiepnn.remas.feature.inventory.item.repository.ItemImageRepository;
import com.hiepnn.remas.feature.upload.repository.TemporaryUploadRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import com.hiepnn.remas.feature.inventory.item.model.ItemPricingResponse;
import com.hiepnn.remas.feature.inventory.item.repository.ItemPricingRepository;
import com.hiepnn.remas.common.annotation.Auditable;
import com.hiepnn.remas.common.constant.AuditAction;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemService {
  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final ItemImageRepository itemImageRepository;
  private final TemporaryUploadRepository temporaryUploadRepository;
  private final ItemPricingRepository itemPricingRepository;

  private ItemResponse mapToResponse(Item item) {
    List<ItemImage> images = itemImageRepository.findByItemId(item.getId());
    List<ItemImageResponse> pictures = images.stream()
        .map(img -> ItemImageResponse.builder()
            .url(img.getImageUrl())
            .note(img.getNote())
            .build())
        .toList();

    List<ItemPricingResponse> pricings = itemPricingRepository.findByItemId(item.getId()).stream()
        .map(pricing -> ItemPricingResponse.builder()
            .id(pricing.getId())
            .itemId(item.getId())
            .priceType(pricing.getPriceType())
            .price(pricing.getPrice())
            .suggestedDeposit(pricing.getSuggestedDeposit())
            .isActive(pricing.getIsActive())
            .build())
        .toList();

    return ItemResponse.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
        .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
        .userId(item.getUser() != null ? item.getUser().getId() : null)
        .username(item.getUser() != null ? item.getUser().getUsername() : null)
        .status(item.getStatus())
        .createdAt(item.getCreatedAt())
        .updatedAt(item.getUpdatedAt())
        .pictures(pictures)
        .pricings(pricings)
        .build();
  }

  // #region Get all
  @Transactional(readOnly = true)
  public PagingResponse<ItemResponse> getAllItems() {
    boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
    String username = SecurityUtils.getCurrentUsername();

    List<Item> list;
    if (isSuperAdmin) {
      list = itemRepository.findAllByStatus(ItemStatus.AVAILABLE, Sort.by(Sort.Direction.DESC, "updatedAt"));
    } else {
      list = itemRepository.findAllByStatusAndUserUsername(ItemStatus.AVAILABLE, username,
          Sort.by(Sort.Direction.DESC, "updatedAt"));
    }
    List<ItemResponse> content = list.stream().map(this::mapToResponse).toList();
    return PagingResponse.<ItemResponse>builder()
        .data(content)
        .total(content.size())
        .page(1)
        .pageSize(content.size())
        .build();
  }
  // #endregion

  // #region Get all by category
  @Transactional(readOnly = true)
  public PagingResponse<ItemResponse> getAllItemsByCategory(Integer categoryId) {
    List<Item> list = itemRepository.findAllByCategoryIdAndStatusNot(categoryId, ItemStatus.DELETED, Sort.by(Sort.Direction.DESC, "createdAt"));
    List<ItemResponse> content = list.stream().map(this::mapToResponse).toList();
    return PagingResponse.<ItemResponse>builder()
        .data(content)
        .total(content.size())
        .page(1)
        .pageSize(content.size())
        .build();
  }
  // #endregion

  // #region Paging & filter
  @Transactional(readOnly = true)
  public PagingResponse<ItemResponse> getItemsWithFilter(int page, int pageSize, String search, String sortField,
      String sortOrder, List<ItemStatus> status, Integer categoryId, Integer ownerId) {
    boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
    String username = SecurityUtils.getCurrentUsername();

    Specification<Item> spec = (root, query, cb) -> {
      // Eager load category and user to prevent N+1 query
      if (query.getResultType() != Long.class && query.getResultType() != long.class) {
        root.fetch("category", JoinType.LEFT);
        root.fetch("user", JoinType.LEFT);
      }

      Predicate p = cb.conjunction();

      if (status != null && !status.isEmpty()) {
        p = cb.and(p, root.get("status").in(status));
      } else {
        p = cb.and(p, cb.notEqual(root.get("status"), ItemStatus.DELETED));
      }

      if (categoryId != null) {
        p = cb.and(p, cb.equal(root.get("category").get("id"), categoryId));
      }

      if (ownerId != null) {
        p = cb.and(p, cb.equal(root.get("user").get("id"), ownerId));
      }

      if (!isSuperAdmin && username != null) {
        p = cb.and(p, cb.equal(root.get("user").get("username"), username));
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
    Page<Item> itemPage = itemRepository.findAll(spec, pageable);

    List<ItemResponse> content = itemPage.getContent().stream()
        .map(this::mapToResponse)
        .toList();

    return PagingResponse.<ItemResponse>builder()
        .data(content)
        .total(itemPage.getTotalElements())
        .page(page)
        .pageSize(pageSize)
        .build();
  }
  // #endregion

  // #region Get by id
  @Transactional(readOnly = true)
  public ItemResponse getItemById(Integer id) {
    Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found!"));
    if (item.getStatus() == ItemStatus.DELETED) {
      throw new ResourceNotFoundException("Item not found!");
    }

    boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
    String username = SecurityUtils.getCurrentUsername();

    if (!isSuperAdmin && (item.getUser() == null || !item.getUser().getUsername().equals(username))) {
      throw new AccessDeniedException("You do not have permission to access this item!");
    }

    return mapToResponse(item);
  }
  // #endregion

  // #region Create
  @Transactional
  @Auditable(action = AuditAction.CREATE_ITEM, description = "'Tạo thiết bị mới: ' + #result.name")
  public ItemResponse createItem(ItemRequest request) {
    if (itemRepository.existsByName(request.getName())) {
      throw new BadRequestException("Item name already exists!");
    }

    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

    String username = SecurityUtils.getCurrentUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found!"));

    Item item = Item.builder()
        .name(request.getName())
        .description(request.getDescription())
        .category(category)
        .user(user)
        .status(ItemStatus.AVAILABLE)
        .build();

    Item savedItem = itemRepository.save(item);
    saveItemImages(savedItem, request.getPictures());

    return mapToResponse(savedItem);
  }
  // #endregion

  // #region Update
  @Transactional
  @Auditable(action = AuditAction.UPDATE_ITEM, description = "'Cập nhật thiết bị: ' + #result.name")
  public ItemResponse updateItem(Integer id, ItemRequest request) {
    Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found!"));
    if (item.getStatus() == ItemStatus.DELETED) {
      throw new ResourceNotFoundException("Item not found!");
    }

    if (itemRepository.existsByNameAndIdNot(request.getName(), id)) {
      throw new BadRequestException("Item name already exists!");
    }

    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

    item.setName(request.getName());
    item.setDescription(request.getDescription());
    item.setCategory(category);
    if (request.getStatus() != null) {
      item.setStatus(request.getStatus());
    }

    Item savedItem = itemRepository.save(item);

    // Delete old images and save new ones
    itemImageRepository.deleteByItemId(id);
    saveItemImages(savedItem, request.getPictures());

    return mapToResponse(savedItem);
  }

  private void saveItemImages(Item item, List<ItemImageRequest> pictures) {
    if (pictures != null) {
      for (int i = 0; i < pictures.size(); i++) {
        ItemImageRequest pic = pictures.get(i);
        ItemImage image = ItemImage.builder()
            .item(item)
            .imageUrl(pic.getUrl())
            .isThumbnail(i == 0)
            .note(pic.getNote())
            .build();
        itemImageRepository.save(image);

        temporaryUploadRepository.findByImageUrl(pic.getUrl()).ifPresent(temporaryUploadRepository::delete);
      }
    }
  }
  // #endregion

  // #region Delete
  @Transactional
  @Auditable(action = AuditAction.DELETE_ITEM, description = "'Xóa thiết bị số #' + #id")
  public void deleteItem(Integer id) {
    Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found!"));

    item.setStatus(ItemStatus.DELETED);
    itemRepository.save(item);
  }
  // #endregion
}
