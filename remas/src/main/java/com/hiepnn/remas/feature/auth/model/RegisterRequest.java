package com.hiepnn.remas.feature.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Contain only letters, numbers, dots, or underscores")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(regexp = "^(?=.*[a-z]).*$", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = "^(?=.*[A-Z]).*$", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = "^(?=.*\\d).*$", message = "Password must contain at least one digit")
    @Pattern(regexp = "^(?=.*[^a-zA-Z0-9]).*$", message = "Password must contain at least one special character")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    private String fullName;
}
