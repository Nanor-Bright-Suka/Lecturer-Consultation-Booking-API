package com.backend.lcbapi.auth.controller;


import com.backend.lcbapi.auth.dto.request.LecturerRegistrationRequestDto;
import com.backend.lcbapi.auth.dto.request.LecturerRegistrationResponseDto;
import com.backend.lcbapi.auth.dto.request.LoginRequestDto;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.service.LecturerAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerAuthService lecturerAuthService;

    @PostMapping("/lecturer/register")
    public ResponseEntity<LecturerRegistrationResponseDto> register(@Valid @RequestBody LecturerRegistrationRequestDto request){
        LecturerRegistrationResponseDto response = lecturerAuthService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/lecturer/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResponseDto loginResponse =  lecturerAuthService.login(request, response);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
















}
