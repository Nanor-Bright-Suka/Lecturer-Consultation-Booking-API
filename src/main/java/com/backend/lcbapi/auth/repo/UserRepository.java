package com.backend.lcbapi.auth.repo;

import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.auth.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUserId(UUID userId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findFirstByRoles_RoleName(RoleEnum roleName);
}
