package com.backend.lcbapi.booking.mapper;


import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.entity.BookingEntity;
import org.springframework.stereotype.Component;



@Component
public class BookingMapper {



    public CreateBookingResponseDto toResponseDto(BookingEntity e){
        return CreateBookingResponseDto.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .slotId(e.getSlot().getId())
                .status(e.getStatus())
                .bookedAt(e.getBookedAt())
                .cancelledAt(e.getCancelledAt())
                .cancellationReason(e.getCancellationReason())
                .completedAt(e.getCompletedAt())
                .attendanceStatus(e.getAttendanceStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }



}
