package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.booking.enums.BookingStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record LecturerBookingViewDto(
        UUID id,
        BookingStatusEnum status,
        LocalDateTime bookedAt,
        StudentDto student,
        ConsultationDto consultation,
        AttendanceOutcomeDto outcome,
        CancelBookingResponseDto cancel
) {
}
