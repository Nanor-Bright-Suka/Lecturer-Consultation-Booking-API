package com.backend.lcbapi.notification.repo;

import com.backend.lcbapi.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepo extends JpaRepository<NotificationEntity, UUID> {



    List<NotificationEntity> findAllByUserUserId(UUID userId);


    Optional<NotificationEntity> findByIdAndUserUserId(
            UUID notificationId,
            UUID userId
    );

}
