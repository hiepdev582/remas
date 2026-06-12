package com.hiepnn.remas.feature.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResult {
    private AuthResponse authResponse;
    private String refreshToken;
}
