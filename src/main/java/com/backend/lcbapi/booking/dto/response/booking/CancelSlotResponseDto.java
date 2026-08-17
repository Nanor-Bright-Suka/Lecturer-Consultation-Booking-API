package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;

import java.util.UUID;

public record CancelSlotResponseDto(
        UUID slotId,

        BookableSlotStatusEnum slotStatus


) {
}
