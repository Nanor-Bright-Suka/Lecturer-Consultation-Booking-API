package com.backend.lcbapi.notification.entity;


import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class NotificationEntity {

    @Id
    private UUID id;

    private NotificationType type;

    private String title;

    private String message;

    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private UserEntity user;






}
