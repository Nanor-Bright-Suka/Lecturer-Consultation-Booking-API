package com.backend.lcbapi.booking.dto.response.consultation;

import java.util.UUID;

public record LecturerReportInfo(
        UUID lecturerId,

        String name,

        String email,

        String staffId,

        String department
) {
}
