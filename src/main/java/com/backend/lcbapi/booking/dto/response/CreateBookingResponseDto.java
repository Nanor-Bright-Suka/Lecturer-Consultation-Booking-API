package com.backend.lcbapi.booking.dto.response;


import com.backend.lcbapi.booking.enums.AttendanceStatusEnum;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@Builder
public class CreateBookingResponseDto {

   private UUID id;

   private UUID studentId;

   private UUID slotId;

   private BookingStatusEnum status;

   private LocalDateTime bookedAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    private LocalDateTime completedAt;


    private AttendanceStatusEnum attendanceStatus;

   private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
