package com.backend.lcbapi.auth.mapper;

import com.backend.lcbapi.auth.dto.request.LecturerRegistrationResponseDto;
import com.backend.lcbapi.auth.entity.LecturerEntity;
import org.springframework.stereotype.Component;


@Component
public class LecturerMapper {

    public LecturerRegistrationResponseDto toResponse(LecturerEntity lecturer) {

        return LecturerRegistrationResponseDto.builder()
                .id(lecturer.getId())
                .staffId(lecturer.getStaffId())
                .email(lecturer.getUser().getEmail())
                .firstName(lecturer.getUser().getFirstName())
                .lastName(lecturer.getUser().getLastName())
                .department(lecturer.getDepartment())
                .build();
    }

}
