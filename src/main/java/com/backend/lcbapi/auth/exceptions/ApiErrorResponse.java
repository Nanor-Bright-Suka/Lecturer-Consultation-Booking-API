package com.backend.lcbapi.auth.exceptions;


import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ApiErrorResponse(
        int status,
        String message,
        String path,
        Instant timestamp,
        List<FieldErrorResponse> errors

) {

    public ApiErrorResponse(int status, String message, String path) {
        this(status, message, path, Instant.now(), List.of());
    }

//    public ApiErrorResponse(int status, String message, String path, List<FieldErrorResponse> errors) {
//        this(status, message, path, Instant.now(), errors);
//    }
}
