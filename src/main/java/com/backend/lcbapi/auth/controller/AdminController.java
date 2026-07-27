package com.backend.lcbapi.auth.controller;


import com.backend.lcbapi.auth.dto.request.AdminLoginRequestDto;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.service.AdminAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAuthService  adminAuthService;

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody AdminLoginRequestDto request, HttpServletResponse response) {

        LoginResponseDto loginResponse = adminAuthService.login(request, response);

        return ResponseEntity.ok(loginResponse);
    }




}
