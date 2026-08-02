package com.backend.lcbapi.shared.exceptions;

public record FieldErrorResponse(
        String field,
        String message
) {
}
