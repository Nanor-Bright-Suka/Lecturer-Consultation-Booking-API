package com.backend.lcbapi.auth.dto.response;

import com.backend.lcbapi.auth.entity.RefreshTokenEntity;

public record RefreshTokenResponseDto(
        String plainToken,
        RefreshTokenEntity refreshTokenEntity
) {
}
