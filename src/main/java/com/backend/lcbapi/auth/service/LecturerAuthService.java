package com.backend.lcbapi.auth.service;


import com.backend.lcbapi.auth.dto.request.LecturerRegistrationRequestDto;
import com.backend.lcbapi.auth.dto.request.LecturerRegistrationResponseDto;
import com.backend.lcbapi.auth.dto.request.LoginRequestDto;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.dto.response.RefreshTokenResponseDto;
import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.RoleEntity;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.enums.RoleEnum;
import com.backend.lcbapi.shared.exceptions.InvalidCredentialException;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import com.backend.lcbapi.shared.exceptions.ConflictException;
import com.backend.lcbapi.auth.mapper.LecturerMapper;
import com.backend.lcbapi.auth.repo.LecturerRepository;
import com.backend.lcbapi.auth.repo.RoleRepository;
import com.backend.lcbapi.auth.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LecturerAuthService {

    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final LecturerMapper lecturerMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final CookieService cookieService;



    public LecturerRegistrationResponseDto register(LecturerRegistrationRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (lecturerRepository.existsByStaffId(request.getStaffId())) {
            throw new ConflictException("Staff ID already exists");
        }

        RoleEntity studentRole = roleRepository.findByRoleName(RoleEnum.ROLE_LECTURER)
                .orElseThrow(() -> new NotFoundException("Lecturer role not found."));

        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user.addRole(studentRole);

        LecturerEntity lecturer = LecturerEntity.builder()
                .id(UUID.randomUUID())
                .staffId(request.getStaffId())
                .department(request.getDepartment())
                .user(user)
                .build();
        user.setLecturer(lecturer);

        UserEntity savedUser = userRepository.save(user);

        return lecturerMapper.toResponse(savedUser.getLecturer());

    }



    @Transactional
    public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response) {

        LecturerEntity lecturer = lecturerRepository.findByStaffId(request.getIdentifier())
                .orElseThrow(() -> new InvalidCredentialException("Invalid credentials."));

        UserEntity user = lecturer.getUser();
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatch) {
            throw new InvalidCredentialException("Invalid credentials.");
        }

        String accessToken = tokenService.generateAccessToken(user);
        RefreshTokenResponseDto refreshToken = tokenService.generateRefreshToken(user);

        cookieService.addRefreshTokenCookie(response, refreshToken.plainToken());

        user.getRefreshTokens().add(refreshToken.refreshTokenEntity());

        userRepository.save(user);

        return new LoginResponseDto (accessToken);
    }














}
