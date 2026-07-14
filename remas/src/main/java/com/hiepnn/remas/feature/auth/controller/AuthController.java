package com.hiepnn.remas.feature.auth.controller;

import com.hiepnn.remas.feature.auth.model.AuthResponse;
import com.hiepnn.remas.feature.auth.model.LoginRequest;
import com.hiepnn.remas.feature.auth.model.LoginResult;
import com.hiepnn.remas.feature.auth.model.RegisterRequest;
import com.hiepnn.remas.feature.auth.service.AuthService;

import com.hiepnn.remas.feature.auth.util.CookieUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String result = authService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Trả về chuỗi Token JWT cùng thông tin tài khoản và quyền hạn")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult loginResult = authService.login(request);

        ResponseCookie cookie = CookieUtils.createRefreshTokenCookie(loginResult.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResult.getAuthResponse());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới Token truy cập", description = "Đọc Refresh Token từ HttpOnly Cookie và cấp lại Access Token mới")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất hệ thống", description = "Đưa token hiện tại vào danh sách đen (Blacklist) để vô hiệu hóa")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String result = authService.logout(authHeader);
        ResponseCookie cookie = CookieUtils.deleteRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result);
    }

}
