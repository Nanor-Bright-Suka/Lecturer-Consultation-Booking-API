package com.backend.lcbapi.booking.dto.response.booking;

import com.backend.lcbapi.awmodule.enums.AvailabilityModeEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsultationDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AvailabilityModeEnum mode,
        String venue,
        String meetingLink,
        String callInstruction
) {
}
