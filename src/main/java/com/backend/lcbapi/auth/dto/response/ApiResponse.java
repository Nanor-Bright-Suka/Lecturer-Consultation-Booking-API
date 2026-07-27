package com.backend.lcbapi.auth.dto.response;

import java.time.Instant;

public record ApiResponse(
        Instant timeStamp,
        int status,
        String message
) {
}
