package com.backend.lcbapi.auth.controller;


import com.backend.lcbapi.auth.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(
                new ApiResponse(
                        Instant.now(),
                        200,
                        "Hello from the health service, I'm working perfectly well"
                )
        );
    }
}
