package com.hiepnn.remas.feature.user.model;

import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Contain only letters, numbers, dots, or underscores")
    private String username;

    private String password;

    @Email(message = "Invalid email format")
    private String email;

    private String fullName;

    private Boolean isActive;

    @NotEmpty(message = "At least one role is required")
    private List<String> roles;
}
