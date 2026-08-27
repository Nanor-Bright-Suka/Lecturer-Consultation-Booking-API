package com.backend.lcbapi.booking.dto.response.consultation;


import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import com.backend.lcbapi.booking.enums.MeetingReportStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeetingReportResponseDto(
        UUID reportId,

        MeetingReportStatusEnum reportStatus,

        BookingStatusEnum bookingStatus,

        LocalDateTime createdAt
) {
}
