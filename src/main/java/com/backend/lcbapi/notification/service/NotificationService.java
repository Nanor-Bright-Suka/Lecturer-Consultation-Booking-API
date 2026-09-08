package com.backend.lcbapi.notification.service;


import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.service.AuthenticatedUserService;
import com.backend.lcbapi.notification.dto.NotificationResponseDto;
import com.backend.lcbapi.notification.entity.NotificationEntity;
import com.backend.lcbapi.notification.enums.NotificationType;
import com.backend.lcbapi.notification.mapper.NotificationMapper;
import com.backend.lcbapi.notification.repo.NotificationRepo;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final NotificationMapper notificationMapper;
    private final Clock clock;
    private final AuthenticatedUserService authenticatedUserService;
    private final WebsocketNotificationService  websocketNotificationService;

    public void createNotification(
            UserEntity recipient,
            String title,
            String message,
            NotificationType type
    ) {

        NotificationEntity notification = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .user(recipient)
                .title(title)
                .message(message)
                .type(type)
                .sentAt(LocalDateTime.now(clock))
                .build();

        NotificationEntity savedNotification = notificationRepo.save(notification);

        NotificationResponseDto notificationResponseDto = notificationMapper.toDto(savedNotification);

        websocketNotificationService.sendNotification(
                recipient,
                notificationResponseDto
        );

    }


    public List<NotificationResponseDto> getAllNotifications() {

        UUID userId =  authenticatedUserService.getCurrentUserId();

        List<NotificationEntity> notifications = notificationRepo.findAllByUserUserId(userId);

        return notificationMapper.toDtoList(notifications);
    }



    public NotificationResponseDto getNotification(UUID notificationId) {

        UUID userId =  authenticatedUserService.getCurrentUserId();

        NotificationEntity notification =
                                            notificationRepo
                                                    .findByIdAndUserUserId(notificationId, userId)
                                                    .orElseThrow(() -> new NotFoundException("Notification not found"));

        return notificationMapper.toDto(notification);
    }














}
