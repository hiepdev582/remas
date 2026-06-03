package com.hiepnn.remas.feature.auth.controller;

import com.hiepnn.remas.feature.auth.model.AuthResponse;
import com.hiepnn.remas.feature.auth.model.LoginRequest;
import com.hiepnn.remas.feature.auth.model.RegisterRequest;
import com.hiepnn.remas.feature.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints API xử lý Đăng ký, Đăng nhập, Đăng xuất")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới", description = "Mặc định gán quyền CUSTOMER khi đăng ký thành công")
    public ResponseEntity<String> register(@Validated @RequestBody RegisterRequest request) {
        String result = authService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Trả về chuỗi Token JWT cùng thông tin tài khoản và quyền hạn")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
