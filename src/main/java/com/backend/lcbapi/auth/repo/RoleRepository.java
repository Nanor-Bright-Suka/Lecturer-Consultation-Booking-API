package com.backend.lcbapi.auth.repo;

import com.backend.lcbapi.auth.entity.RoleEntity;
import com.backend.lcbapi.auth.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByRoleName(RoleEnum roleName);

}
