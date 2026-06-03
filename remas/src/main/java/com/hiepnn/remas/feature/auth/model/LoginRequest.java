package com.hiepnn.remas.feature.auth.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
