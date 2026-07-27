package com.backend.lcbapi.auth.service;


import com.backend.lcbapi.auth.dto.response.RefreshTokenResponseDto;
import com.backend.lcbapi.auth.utility.Utility;
import com.backend.lcbapi.auth.config.SecurityEnvironment;
import com.backend.lcbapi.auth.entity.RefreshTokenEntity;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.repo.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final SecurityEnvironment securityEnvironment;
    private final Utility utility;

    private SecretKey getSigningKey() {
        String token = securityEnvironment.getToken();
        byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }


    public static Set<String> extractRoles(UserEntity user) {
        return user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
    }

    public static Set<String> extractPermissions(UserEntity user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(perm -> perm.getPermissionName().name())
                .collect(Collectors.toSet());
    }


    public String generateAccessToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        Set<String> roles = extractRoles(user);
        Set<String> permissions = extractPermissions(user);

        claims.put("roles", roles);
        claims.put("permissions", permissions);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(String.valueOf(user.getUserId()))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(securityEnvironment.getAccessTokenExpirationInMinutes(), ChronoUnit.MINUTES)))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public RefreshTokenResponseDto generateRefreshToken(UserEntity user) {

        String plainToken = UUID.randomUUID().toString();
        String tokenHash = utility.hashToken(plainToken);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(securityEnvironment.getRefreshTokenExpirationInDays(), ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .revoked(false)
                .build();
        return new RefreshTokenResponseDto(plainToken, refreshToken);
    }


}



