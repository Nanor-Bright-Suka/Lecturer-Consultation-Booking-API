package com.backend.lcbapi.awmodule.dto.response;

import lombok.Builder;

import java.util.UUID;



@Builder
public record LecturerAvailabilitySummaryDto(
         UUID id,
         String firstName,
         String lastName,
         String department
) {
}
