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

    List<AvailabilityWindowEntity> findAllByLecturerId(UUID lecturerId);


    void deleteByLecturerId(UUID id);



    List<AvailabilityWindowEntity> findAllByStatusNot(AvailabilityWindowStatusEnum status);

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM AvailabilityWindowEntity a
    WHERE a.lecturer.id = :lecturerId
    AND a.date = :date
    AND a.status <> :status
    AND a.startTime < :endTime
    AND a.endTime > :startTime
""")
    boolean existsConflict(
            UUID lecturerId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            AvailabilityWindowStatusEnum status);


    @Query("""
    SELECT aw
    FROM AvailabilityWindowEntity aw
    WHERE aw.id = :availabilityId
      AND aw.status <> :status
    """)
    Optional<AvailabilityWindowEntity> findByIdAndStatusNot(
           UUID availabilityId,
           AvailabilityWindowStatusEnum status
    );

    @Query("""
        SELECT a
        FROM AvailabilityWindowEntity a
        WHERE a.status = :status
          AND (
                a.date < :currentDate
                OR (
                    a.date = :currentDate
                    AND a.startTime <= :currentTime
                )
              )
        """)
    List<AvailabilityWindowEntity> findWindowsReadyToStart(
            AvailabilityWindowStatusEnum status,
            LocalDate currentDate,
            LocalTime currentTime);


    @Query("""
        SELECT a
        FROM AvailabilityWindowEntity a
        WHERE a.status = :status
          AND (
                a.date < :currentDate
                OR (
                    a.date = :currentDate
                    AND a.endTime <= :currentTime
                )
              )
        """)
    List<AvailabilityWindowEntity> findWindowsReadyToEnd(
            AvailabilityWindowStatusEnum status,
            LocalDate currentDate,
            LocalTime currentTime
    );




}
