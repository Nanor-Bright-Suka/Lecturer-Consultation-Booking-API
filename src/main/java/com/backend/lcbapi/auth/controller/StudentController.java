package com.backend.lcbapi.auth.controller;


import com.backend.lcbapi.auth.dto.request.LoginRequestDto;
import com.backend.lcbapi.auth.dto.request.StudentRegisterRequestDto;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.dto.response.StudentRegisterResponseDto;
import com.backend.lcbapi.auth.service.StudentAuthService;
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
public class StudentController {

    private final StudentAuthService studentAuthService;

    @PostMapping("/student/register")
    public ResponseEntity<StudentRegisterResponseDto> register(@Valid @RequestBody StudentRegisterRequestDto request){
        StudentRegisterResponseDto response = studentAuthService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
    LoginResponseDto loginResponse =  studentAuthService.login(request, response);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }






}
