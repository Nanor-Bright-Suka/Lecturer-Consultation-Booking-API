package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.booking.enums.BaseRoleEnum;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record CancelBookingResponseDto(
        UUID bookingId,
     LocalDateTime cancelledAt,
     BookingStatusEnum bookingStatus,
     BookableSlotStatusEnum slotStatus,
     BaseRoleEnum cancelledBy

) {
}



