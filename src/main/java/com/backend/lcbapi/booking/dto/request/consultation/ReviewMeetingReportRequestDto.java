package com.backend.lcbapi.booking.dto.request.consultation;


import com.backend.lcbapi.booking.enums.MeetingReportDecisionEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewMeetingReportRequestDto(

        @NotNull(message = "Review decision is required")
        MeetingReportDecisionEnum decision,

        @Size(
                max = 1000,
                message = "Review reason must not exceed 1000 characters"
        )
        String reason
) {
}
