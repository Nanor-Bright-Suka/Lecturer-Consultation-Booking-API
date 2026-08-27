package com.backend.lcbapi.booking.dto.response.consultation;

import java.util.UUID;

public record StudentReportInfo(
        UUID id,
        String studentId,
        String name,
        String email
) {
}
