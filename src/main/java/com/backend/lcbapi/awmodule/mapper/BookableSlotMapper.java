package com.backend.lcbapi.awmodule.mapper;


import com.backend.lcbapi.awmodule.dto.response.BookableSlotResponseDto;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.booking.dto.response.booking.CancelSlotResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookableSlotMapper {



        public BookableSlotResponseDto toDto(BookableSlotEntity entity) {

            return BookableSlotResponseDto.builder()
                    .id(entity.getId())
                    .date(entity.getDate())
                    .startTime(entity.getStartTime())
                    .endTime(entity.getEndTime())
                    .status(entity.getStatus())
                    .build();
        }


        public List<BookableSlotResponseDto> toDtoList(List<BookableSlotEntity> entities) {
            return entities.stream()
                    .map(this::toDto)
                    .toList();
        }


    public CancelSlotResponseDto toCancelSlotResponse(BookableSlotEntity slot) {

        return new CancelSlotResponseDto(
                slot.getId(),
                slot.getStatus()
        );
    }



}
