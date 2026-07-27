package com.backend.lcbapi.auth.mapper;


import com.backend.lcbapi.auth.dto.response.StudentRegisterResponseDto;
import com.backend.lcbapi.auth.entity.StudentEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentRegisterResponseDto toResponse(StudentEntity student){

        return StudentRegisterResponseDto.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .email(student.getUser().getEmail())
                .firstName(student.getUser().getFirstName())
                .lastName(student.getUser().getLastName())
                .build();
    }




}
