package com.backend.lcbapi.notification.dto;


import com.backend.lcbapi.notification.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDto(

        UUID id,
        NotificationType notificationType,
        String notificationTitle,
        String content,
        LocalDateTime sentAt
) {
}
