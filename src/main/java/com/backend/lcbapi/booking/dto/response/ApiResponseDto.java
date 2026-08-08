package com.backend.lcbapi.booking.dto.response;




public record ApiResponseDto<T>(
        String message,
        T data
) {
}
