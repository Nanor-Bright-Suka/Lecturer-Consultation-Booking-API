package com.backend.lcbapi.awmodule.repo;

import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvailabilityWindowRepo extends JpaRepository<AvailabilityWindowEntity, UUID> {
    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM AvailabilityWindowEntity a
    WHERE a.lecturer.id = :lecturerId
    AND a.date = :date
    AND a.startTime < :endTime
    AND a.endTime > :startTime
""")
    boolean existsConflict(UUID lecturerId, LocalDate date, LocalTime startTime, LocalTime endTime);

List<AvailabilityWindowEntity> findAllByLecturerIdAndStatus(UUID lecturerId, AvailabilityWindowStatusEnum status);

    Optional<AvailabilityWindowEntity> findByIdAndStatus(UUID id, AvailabilityWindowStatusEnum status);

}
