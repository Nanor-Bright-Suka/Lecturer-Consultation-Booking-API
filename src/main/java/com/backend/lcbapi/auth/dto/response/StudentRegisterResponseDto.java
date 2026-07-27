package com.backend.lcbapi.auth.dto.response;

import lombok.Builder;

import java.util.UUID;


@Builder
public record StudentRegisterResponseDto(
         UUID id,
         String firstName,
         String lastName,
         String email,
         String studentId
) {
}
