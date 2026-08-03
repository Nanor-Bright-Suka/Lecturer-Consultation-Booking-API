package com.backend.lcbapi.awmodule.mapper;


import com.backend.lcbapi.awmodule.dto.request.CreateAvailabilityWindowRequestDto;
import com.backend.lcbapi.awmodule.dto.response.AvailabilityWindowResponseDto;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class AvailabilityWindowMapper {


    public AvailabilityWindowEntity toEntity(CreateAvailabilityWindowRequestDto request) {
     return AvailabilityWindowEntity.builder()
                .id(UUID.randomUUID())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotDuration(request.getSlotDuration())
                .venue(request.getVenue())
                .mode(request.getMode())
                .meetingLink(request.getMeetingLink())
                .callInstruction(request.getCallInstruction())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public AvailabilityWindowResponseDto toDto(AvailabilityWindowEntity entity) {

        return AvailabilityWindowResponseDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .slotDuration(entity.getSlotDuration())
                .availabilityMode(entity.getMode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<AvailabilityWindowResponseDto> toResponseList(List<AvailabilityWindowEntity> entities) {

        return entities.stream()
                .map(this::toDto)
                .toList();
    }



}
