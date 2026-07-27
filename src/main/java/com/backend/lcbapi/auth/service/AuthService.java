package com.backend.lcbapi.auth.service;


import com.backend.lcbapi.auth.dto.response.RefreshTokenResponseDto;
import com.backend.lcbapi.auth.entity.RefreshTokenEntity;
import com.backend.lcbapi.auth.exceptions.InvalidCredentialException;
import com.backend.lcbapi.auth.repo.RefreshTokenRepository;
import com.backend.lcbapi.auth.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Utility utility;
    private final TokenService tokenService;


    @Transactional
    public void logout(String rawRefreshToken) {

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new InvalidCredentialException("Invalid refresh token");
        }

        String tokenHash = utility.hashToken(rawRefreshToken);

        RefreshTokenEntity token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidCredentialException("Invalid refresh token"));

        token.setRevoked(true);
        token.setRevokedAt(Instant.now());
    }


    @Transactional
    public Map<String, String> refreshService(String rawRefreshToken){

        String hashedToken = utility.hashToken(rawRefreshToken);

        RefreshTokenEntity storedToken = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new InvalidCredentialException("Invalid  token"));


        if (storedToken.getRevoked()) {
            throw new InvalidCredentialException("Refresh token invalid");
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCredentialException("Refresh token expired");
        }

        // 4. Generate new tokens
        storedToken.setRevoked(true);
        storedToken.setRevokedAt(Instant.now());

        RefreshTokenResponseDto newRefreshToken = tokenService.generateRefreshToken(storedToken.getUser());
        String newAccessToken = tokenService.generateAccessToken(storedToken.getUser());

        refreshTokenRepository.save(newRefreshToken.refreshTokenEntity());

        return Map.of(
                "refreshToken", newRefreshToken.plainToken(),
              "accessToken", newAccessToken
        );

    }

















}
