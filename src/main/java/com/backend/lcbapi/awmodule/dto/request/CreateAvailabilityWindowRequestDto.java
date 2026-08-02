package com.backend.lcbapi.awmodule.dto.request;


import com.backend.lcbapi.awmodule.enums.AvailabilityModeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
public class CreateAvailabilityWindowRequestDto {

        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        private LocalTime endTime;

        @NotNull(message = "Slot duration is required")
        @Positive(message = "Slot duration must be greater than zero")
        private Integer slotDuration;

        private String venue;

        @NotNull(message = "Availability mode is required")
        private AvailabilityModeEnum mode;

        private String meetingLink;

        private String callInstruction;


}
