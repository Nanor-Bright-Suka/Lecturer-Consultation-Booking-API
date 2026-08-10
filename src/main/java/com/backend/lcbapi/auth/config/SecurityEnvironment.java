package com.backend.lcbapi.auth.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Getter
public final class SecurityEnvironment {

    @Value("${jwt.secret}")
    private String token;

    @Value("${jwt.access-token-expiration}")
    private int accessTokenExpirationInMinutes;

    @Value("${jwt.refresh-token-expiration}")
    private int refreshTokenExpirationInDays;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.firstName}")
    private String adminFirstName;

    @Value("${admin.lastName}")
    private String adminLastName;










}
