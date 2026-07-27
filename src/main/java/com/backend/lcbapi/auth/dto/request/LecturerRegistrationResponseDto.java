package com.backend.lcbapi.auth.dto.request;

import lombok.Builder;

import java.util.UUID;


@Builder
public record LecturerRegistrationResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String staffId,
        String email,
        String department
) {
}
