package com.backend.lcbapi.awmodule.mapper;


import com.backend.lcbapi.awmodule.dto.response.BookableSlotResponseDto;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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


}
