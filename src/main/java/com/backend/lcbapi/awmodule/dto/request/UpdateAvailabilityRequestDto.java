package com.backend.lcbapi.awmodule.dto.request;


import com.backend.lcbapi.awmodule.enums.AvailabilityModeEnum;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class UpdateAvailabilityRequestDto {

        private LocalDate date;

        private LocalTime startTime;

        private LocalTime endTime;

        @Positive(message = "Slot duration must be positive")
        private Integer slotDuration;

        private String venue;

        private AvailabilityModeEnum mode;

        private String meetingLink;

        private String callInstruction;

}
