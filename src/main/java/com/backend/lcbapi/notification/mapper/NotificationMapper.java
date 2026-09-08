package com.backend.lcbapi.notification.mapper;

import com.backend.lcbapi.notification.dto.NotificationResponseDto;
import com.backend.lcbapi.notification.entity.NotificationEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class NotificationMapper {



    public NotificationResponseDto toDto(NotificationEntity entity) {

        return  new NotificationResponseDto(
                entity.getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getSentAt()
        );
    }



    public List<NotificationResponseDto> toDtoList(List<NotificationEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }






}
