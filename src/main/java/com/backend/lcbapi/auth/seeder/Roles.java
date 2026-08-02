package com.backend.lcbapi.auth.seeder;


import com.backend.lcbapi.auth.entity.PermissionEntity;
import com.backend.lcbapi.auth.entity.RoleEntity;
import com.backend.lcbapi.auth.enums.PermissionEnum;
import com.backend.lcbapi.auth.enums.RoleEnum;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import com.backend.lcbapi.auth.repo.PermissionRepository;
import com.backend.lcbapi.auth.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Order(value=2)
public class Roles implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(ApplicationArguments args) {

        // USER
        RoleEntity normalUser = roleRepository.findByRoleName(RoleEnum.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .id(UUID.randomUUID())
                                .roleName(RoleEnum.ROLE_STUDENT)
                                .createdAt(Instant.now())
                                .build()
                ));

        add(normalUser,
                PermissionEnum.READ_PROFILE,
                PermissionEnum.VIEW_ALL_AVAILABILITY_WINDOW,
                PermissionEnum.VIEW_ALL_BOOKABLE_SLOTS



        );


        RoleEntity lecturer = roleRepository.findByRoleName(RoleEnum.ROLE_LECTURER)
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .id(UUID.randomUUID())
                                .roleName(RoleEnum.ROLE_LECTURER)
                                .createdAt(Instant.now())
                                .build()
                ));

        add(lecturer,
                PermissionEnum.CREATE_PROFILE,
                PermissionEnum.CREATE_AVAILABILITY_WINDOW,
                PermissionEnum.VIEW_ALL_AVAILABILITY_WINDOW,
                PermissionEnum.VIEW_ALL_BOOKABLE_SLOTS,
                PermissionEnum.UPDATE_AVAILABILITY_WINDOW,
                PermissionEnum.DELETE_AVAILABILITY_WINDOW

        );


        // ADMIN
        RoleEntity admin = roleRepository.findByRoleName(RoleEnum.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .id(UUID.randomUUID())
                                .roleName(RoleEnum.ROLE_ADMIN)
                                .createdAt(Instant.now())
                                .build()
                ));

        add(admin,
                PermissionEnum.UPDATE_PROFILE

                );





    }

    private void add(RoleEntity role, PermissionEnum... perms) {
        for (PermissionEnum p : perms) {
            PermissionEntity perm = permissionRepository.findByPermissionName(p).orElseThrow(() ->
                    new NotFoundException("PermissionEntity not found" + p.name()));
            role.addPermission(perm);
        }
        roleRepository.save(role);
    }



}
