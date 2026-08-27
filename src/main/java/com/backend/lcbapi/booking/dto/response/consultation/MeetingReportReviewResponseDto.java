package com.backend.lcbapi.booking.dto.response.consultation;


import com.backend.lcbapi.booking.enums.AttendanceStatus;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import com.backend.lcbapi.booking.enums.MeetingReportStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeetingReportReviewResponseDto(
        UUID reportId,

        MeetingReportStatusEnum reportStatus,

        BookingStatusEnum bookingStatus,

        AttendanceStatus.AttendanceStatusEnum attendanceStatus,

        LocalDateTime reviewedAt
) {
}
