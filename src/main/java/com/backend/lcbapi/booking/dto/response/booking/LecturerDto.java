package com.backend.lcbapi.booking.dto.response.booking;

import java.util.UUID;

public record LecturerDto(
        UUID id,
        String firstName,
        String lastName,
        String department
) {
}
