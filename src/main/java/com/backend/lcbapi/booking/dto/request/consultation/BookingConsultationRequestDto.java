package com.backend.lcbapi.booking.dto.request.consultation;


import com.backend.lcbapi.booking.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookingConsultationRequestDto {


    @NotNull(message = "Attendance status is required")
  private AttendanceStatus.AttendanceStatusEnum attendanceStatus;
}
