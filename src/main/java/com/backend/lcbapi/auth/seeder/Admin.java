package com.backend.lcbapi.auth.seeder;

import com.backend.lcbapi.auth.config.SecurityEnvironment;
import com.backend.lcbapi.auth.entity.RoleEntity;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.enums.RoleEnum;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import com.backend.lcbapi.auth.repo.RoleRepository;
import com.backend.lcbapi.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Order(value = 3)
@Slf4j
public class Admin implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityEnvironment securityEnvironment;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmail(securityEnvironment.getAdminEmail())) return;

        RoleEntity adminRole = roleRepository.findByRoleName(RoleEnum.ROLE_ADMIN)
                .orElseThrow(() -> new NotFoundException("Admin role not found!"));

        UserEntity admin = UserEntity.builder()
                .userId(UUID.randomUUID())
                .firstName(securityEnvironment.getAdminFirstName())
                .lastName(securityEnvironment.getAdminLastName())
                .email(securityEnvironment.getAdminEmail())
                .password(passwordEncoder.encode(securityEnvironment.getAdminPassword()))
                .build();

        admin.addRole(adminRole);
        userRepository.save(admin);
        log.info("Pre-seeded ADMIN created, {} ", securityEnvironment.getAdminFirstName() + " " + securityEnvironment.getAdminLastName());
    }
}
