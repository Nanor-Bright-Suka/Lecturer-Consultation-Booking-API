package com.backend.lcbapi.auth.seeder;


import com.backend.lcbapi.auth.entity.PermissionEntity;
import com.backend.lcbapi.auth.enums.PermissionEnum;
import com.backend.lcbapi.auth.repo.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Order(value = 1)
public class Permission implements ApplicationRunner {

    private final PermissionRepository permissionRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (PermissionEnum p : PermissionEnum.values()) {
            permissionRepository.findByPermissionName(p)
                    .orElseGet(() -> permissionRepository.save(
                            PermissionEntity.builder()
                                    .id(UUID.randomUUID())
                                    .permissionName(p)
                                    .build()
                    ));

        }
    }

}