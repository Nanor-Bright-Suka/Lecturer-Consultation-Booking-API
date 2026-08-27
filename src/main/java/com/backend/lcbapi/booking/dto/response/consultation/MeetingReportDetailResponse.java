package com.backend.lcbapi.booking.dto.response.consultation;


import com.backend.lcbapi.booking.enums.MeetingReportStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeetingReportDetailResponse(
        UUID reportId,

        MeetingReportStatusEnum reportStatus,

        String description,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        StudentReportInfo student,

        LecturerReportInfo lecturer,

        BookingReportInfo booking,

        ReviewReportInfo review
) {
}
