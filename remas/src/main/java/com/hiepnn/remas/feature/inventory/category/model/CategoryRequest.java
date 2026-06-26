package com.hiepnn.remas.feature.inventory.category.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Contain only letters, numbers, dots, or underscores")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
