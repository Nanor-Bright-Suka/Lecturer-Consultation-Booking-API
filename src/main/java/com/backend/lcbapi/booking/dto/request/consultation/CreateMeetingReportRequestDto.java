package com.backend.lcbapi.booking.dto.request.consultation;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMeetingReportRequestDto {



    @NotBlank(message = "Report description is required")
    @Size(
            max = 1000,
            message = "Report description must not exceed 1000 characters"
    )
   private  String description;



}
