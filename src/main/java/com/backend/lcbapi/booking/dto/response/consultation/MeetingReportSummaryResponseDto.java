package com.backend.lcbapi.booking.dto.response.consultation;


import com.backend.lcbapi.booking.enums.MeetingReportStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeetingReportSummaryResponseDto(

        UUID reportId,

        String studentName,

        String lecturerName,

        MeetingReportStatusEnum reportStatus,

        LocalDateTime createdAt



) {
}
