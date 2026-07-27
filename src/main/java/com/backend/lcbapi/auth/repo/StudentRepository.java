package com.backend.lcbapi.auth.repo;

import com.backend.lcbapi.auth.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {
    Optional<StudentEntity> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
}
