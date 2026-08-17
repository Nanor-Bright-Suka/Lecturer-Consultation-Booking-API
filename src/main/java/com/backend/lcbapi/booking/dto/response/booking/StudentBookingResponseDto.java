package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.booking.enums.BookingStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentBookingResponseDto(
        UUID id,
        BookingStatusEnum status,
        LocalDateTime bookedAt,
        LecturerDto lecturer,
        ConsultationDto consultation,
        AttendanceOutcomeDto outcome,
        CancelBookingResponseDto cancel
) {
}
