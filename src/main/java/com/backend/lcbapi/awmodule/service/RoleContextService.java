package com.backend.lcbapi.awmodule.service;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.auth.repo.LecturerRepository;
import com.backend.lcbapi.auth.repo.StudentRepository;
import com.backend.lcbapi.auth.service.AuthenticatedUserService;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleContextService{

    private final AuthenticatedUserService authenticatedUserService;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository   studentRepo;

    public LecturerEntity getCurrentLecturer(){

        UUID userId = authenticatedUserService.getCurrentUserId();

        return lecturerRepository.findByUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("Lecturer profile not found"));
    }

    public StudentEntity getCurrentStudent(){

        UUID userId = authenticatedUserService.getCurrentUserId();

        return studentRepo.findByUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }






}
