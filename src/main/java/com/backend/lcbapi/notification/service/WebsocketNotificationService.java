package com.backend.lcbapi.notification.service;


import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.notification.dto.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(
            UserEntity recipient,
            NotificationResponseDto notification
    ) {

        messagingTemplate.convertAndSendToUser(
                recipient.getUserId().toString(),
                "/queue/notifications",
                notification
        );
    }
}

