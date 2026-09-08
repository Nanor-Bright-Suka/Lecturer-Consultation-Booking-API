package com.backend.lcbapi.notification.controller;


import com.backend.lcbapi.booking.dto.response.ApiResponseDto;
import com.backend.lcbapi.notification.dto.NotificationResponseDto;
import com.backend.lcbapi.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_ALL_NOTIFICATIONS')")
    public ResponseEntity<ApiResponseDto<List<NotificationResponseDto>>> getAllNotifications() {

        List<NotificationResponseDto> notifications =
                                        notificationService.getAllNotifications();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Notification retrieved successfully", notifications));
    }


    @GetMapping("/{notificationId}")
    @PreAuthorize("hasAuthority('VIEW_NOTIFICATION_DETAILS')")
    public ResponseEntity<ApiResponseDto<NotificationResponseDto>> getNotification(@PathVariable UUID notificationId) {


        NotificationResponseDto notification =
                                                notificationService.getNotification(notificationId);

   return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Notification retrieved successfully", notification));
    }

























}
