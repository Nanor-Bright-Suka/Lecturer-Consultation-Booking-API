package com.backend.lcbapi.booking.dto.response.consultation;


import java.time.LocalDateTime;

public record ReviewReportInfo(
        LocalDateTime reviewedAt,
        String reviewReason
) {
}
