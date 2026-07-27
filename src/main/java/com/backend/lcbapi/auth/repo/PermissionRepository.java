package com.backend.lcbapi.auth.repo;

import com.backend.lcbapi.auth.entity.PermissionEntity;
import com.backend.lcbapi.auth.enums.PermissionEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByPermissionName(PermissionEnum permissionName);
}
