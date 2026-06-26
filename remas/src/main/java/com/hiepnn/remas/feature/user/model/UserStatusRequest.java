package com.hiepnn.remas.feature.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusRequest {
    @NotNull(message = "Status (isActive) is required")
    private Boolean isActive;
}
