package com.backend.lcbapi.awmodule.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AvailabilityWindowResponseDto {

    private UUID id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer slotDuration;

    private Integer slotsGenerated;

    private Instant createdAt;

    private Instant updatedAt;

}