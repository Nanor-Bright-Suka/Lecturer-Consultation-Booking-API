package com.backend.lcbapi.auth.service;


import com.backend.lcbapi.auth.dto.request.AdminLoginRequestDto;
import com.backend.lcbapi.auth.dto.response.LoginResponseDto;
import com.backend.lcbapi.auth.dto.response.RefreshTokenResponseDto;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.enums.RoleEnum;
import com.backend.lcbapi.shared.exceptions.InvalidCredentialException;
import com.backend.lcbapi.auth.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final CookieService cookieService;


    @Transactional
    public LoginResponseDto login(AdminLoginRequestDto request, HttpServletResponse response) {

        UserEntity admin = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Invalid credentials."));


        boolean isAdmin = admin.hasRole(RoleEnum.ROLE_ADMIN);

        if (!isAdmin) {
            throw new InvalidCredentialException("Invalid credentials.");
        }

        boolean adminPasswordMatch = passwordEncoder.matches(request.getPassword(), admin.getPassword());

        if (!adminPasswordMatch) {
            throw new InvalidCredentialException("Invalid credentials.");
        }

        String accessToken = tokenService.generateAccessToken(admin);
        RefreshTokenResponseDto refreshToken = tokenService.generateRefreshToken(admin);

        cookieService.addRefreshTokenCookie(response, refreshToken.plainToken());

        admin.getRefreshTokens().add(refreshToken.refreshTokenEntity());

        userRepository.save(admin);

        return new LoginResponseDto(accessToken);
    }





}
