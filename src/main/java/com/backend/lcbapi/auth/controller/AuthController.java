package com.backend.lcbapi.auth.controller;


import com.backend.lcbapi.auth.config.SecurityEnvironment;
import com.backend.lcbapi.auth.dto.response.ApiResponse;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.SecurityService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityEnvironment securityEnvironment;

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);

        // Delete the cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // true in prod, false in dev
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse(Instant.now(),200,"Logged out successfully"));
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {

        Map<String, String> result = authService.refreshService(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", result.get("refreshToken"))
                .httpOnly(true)
                .secure(true) // true in prod, false in dev
                .sameSite("Lax")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(securityEnvironment.getRefreshTokenExpirationInDays()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponseDto(result.get("accessToken")));
    }











}
