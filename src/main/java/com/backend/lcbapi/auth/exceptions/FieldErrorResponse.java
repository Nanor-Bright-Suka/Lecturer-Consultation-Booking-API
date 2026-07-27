package com.backend.lcbapi.auth.exceptions;

public record FieldErrorResponse(
        String field,
        String message
) {
}
