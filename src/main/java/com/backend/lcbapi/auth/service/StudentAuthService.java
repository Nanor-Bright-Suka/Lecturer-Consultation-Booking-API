package com.backend.lcbapi.auth.service;


import com.backend.lcbapi.auth.dto.request.LoginRequestDto;
import com.backend.lcbapi.auth.dto.request.StudentRegisterRequestDto;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.dto.response.RefreshTokenResponseDto;
import com.backend.lcbapi.auth.dto.response.StudentRegisterResponseDto;
import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.RoleEntity;
import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.enums.RoleEnum;
import com.backend.lcbapi.awmodule.repo.AvailabilityWindowRepo;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.shared.exceptions.InvalidCredentialException;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import com.backend.lcbapi.shared.exceptions.ResourceAlreadyExistException;
import com.backend.lcbapi.auth.mapper.StudentMapper;
import com.backend.lcbapi.auth.repo.RoleRepository;
import com.backend.lcbapi.auth.repo.StudentRepository;
import com.backend.lcbapi.auth.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentAuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final CookieService cookieService;
    private final AvailabilityWindowRepo availabilityWindowRepo;
    private final BookableSlotRepo bookableSlotRepo;


    public StudentRegisterResponseDto register(StudentRegisterRequestDto request) {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceAlreadyExistException("Email already exists");
            }

            if (studentRepository.existsByStudentId(request.getStudentId())) {
                throw new ResourceAlreadyExistException("Student ID already exists");
            }

            RoleEntity studentRole = roleRepository.findByRoleName(RoleEnum.ROLE_STUDENT)
                    .orElseThrow(() -> new NotFoundException("Student role not found."));

            UserEntity user = UserEntity.builder()
                    .userId(UUID.randomUUID())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            user.addRole(studentRole);

            StudentEntity student = StudentEntity.builder()
                    .id(UUID.randomUUID())
                    .studentId(request.getStudentId())
                    .user(user)
                    .build();
            user.setStudent(student);

            UserEntity savedUser = userRepository.save(user);

            return studentMapper.toResponse(savedUser.getStudent());

    }





    @Transactional
    public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response) {

        StudentEntity student = studentRepository.findByStudentId(request.getIdentifier())
                .orElseThrow(() -> new InvalidCredentialException("Invalid credentials."));

        UserEntity user = student.getUser();

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



//    @Transactional
//    public UserEntity deleteUserService(UUID userId) {
//
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//
//
//        LecturerEntity lecturer = user.getLecturer();
//
//        if (lecturer != null) {
//
//            List<UUID> availabilityIds = availabilityWindowRepo.findIdsByLecturerId(lecturer.getId());
//
//            bookableSlotRepo.deleteByAvailabilityWindowIdIn(availabilityIds);
//
//            availabilityWindowRepo.deleteByLecturerId(lecturer.getId());
//        }
//
//
//        userRepository.delete(user);
//
//        return user;
//    }






}
