package com.backend.lcbapi.auth.repo;

import com.backend.lcbapi.auth.entity.LecturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LecturerRepository extends JpaRepository<LecturerEntity, UUID> {
    Optional<LecturerEntity> findByStaffId(String staffId);
    boolean existsByStaffId(String staffId);

}
