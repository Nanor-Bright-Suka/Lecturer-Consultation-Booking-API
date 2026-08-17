package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.booking.enums.AttendanceStatusEnum;

import java.time.LocalDateTime;

public record AttendanceOutcomeDto(
        AttendanceStatusEnum attendanceStatus,
        LocalDateTime completedAt
) {
}
