package com.backend.lcbapi.awmodule.dto.response;


import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BookableSlotResponseDto {

        private UUID id;

        private LocalDate date;

        private LocalTime startTime;

        private LocalTime endTime;

        private BookableSlotStatusEnum status;


}
