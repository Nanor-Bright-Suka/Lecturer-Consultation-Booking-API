package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.booking.enums.AttendanceStatus;

import java.time.LocalDateTime;

public record AttendanceOutcomeDto(
        AttendanceStatus.AttendanceStatusEnum attendanceStatus

) {
}
