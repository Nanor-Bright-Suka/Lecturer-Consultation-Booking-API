package com.backend.lcbapi.booking.dto.response.consultation;


import com.backend.lcbapi.awmodule.enums.AvailabilityModeEnum;
import com.backend.lcbapi.booking.enums.AttendanceStatus;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;




public record BookingReportInfo(

        UUID bookingId,

        BookingStatusEnum bookingStatus,

        AttendanceStatus.AttendanceStatusEnum attendanceStatus,

        LocalDate date,

        LocalTime startTime,

        LocalTime endTime,

        AvailabilityModeEnum mode,

        String venue,

        String meetingLink
) {
}
