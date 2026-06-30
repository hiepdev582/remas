package com.hiepnn.remas.feature.inventory.item.model;

import com.hiepnn.remas.common.constant.ItemStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N}._\\s-]+$", message = "Contain only letters, numbers, spaces, dots, underscores, or dashes")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    private ItemStatus status = ItemStatus.AVAILABLE;

    private List<ItemImageRequest> pictures;
}
