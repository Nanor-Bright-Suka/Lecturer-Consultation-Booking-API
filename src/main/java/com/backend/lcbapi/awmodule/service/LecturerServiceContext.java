package com.backend.lcbapi.awmodule.service;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.repo.LecturerRepository;
import com.backend.lcbapi.auth.service.AuthenticatedUserService;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LecturerServiceContext {

    private final AuthenticatedUserService authenticatedUserService;
    private final LecturerRepository lecturerRepository;

    public LecturerEntity getCurrentLecturer(){

        UUID userId = authenticatedUserService.getCurrentUserId();

        return lecturerRepository.findByUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("Lecturer profile not found"));
    }

}
