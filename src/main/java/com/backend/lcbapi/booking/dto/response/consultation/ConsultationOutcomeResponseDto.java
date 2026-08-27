package com.backend.lcbapi.booking.dto.response.consultation;


import com.backend.lcbapi.booking.enums.AttendanceStatus;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;


import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultationOutcomeResponseDto(

        UUID bookingId,

        BookingStatusEnum bookingStatus,

        AttendanceStatus.AttendanceStatusEnum attendanceStatus,

        LocalDateTime completedAt
) {
}
