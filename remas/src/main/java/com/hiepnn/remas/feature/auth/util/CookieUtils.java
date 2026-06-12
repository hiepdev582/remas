package com.hiepnn.remas.feature.auth.util;

import org.springframework.http.ResponseCookie;

public class CookieUtils {
    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // localhost
                .path("/api/v1/auth/refresh")
                .maxAge(60 * 60 * 24 * 7) // 7 days
                .sameSite("Lax")
                .build();
    }
}
