package com.backend.lcbapi.awmodule.repo;

import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookableSlotRepo extends JpaRepository<BookableSlotEntity, UUID> {
    long countByAvailabilityWindowId(UUID availabilityWindowId);
    List<BookableSlotEntity> findAllByAvailabilityWindowId(UUID availabilityWindowId);
    void deleteAllByAvailabilityWindowIdAndStatus(UUID availabilityWindowId, BookableSlotStatusEnum status);
    boolean existsByAvailabilityWindowIdAndStatusNot(UUID availabilityWindowId, BookableSlotStatusEnum status);
    void deleteByAvailabilityWindowLecturerId(UUID lecturerId);

    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT s
    FROM BookableSlotEntity s
    WHERE s.id = :slotId
    """)
    Optional<BookableSlotEntity> findByIdForUpdate(UUID slotId);


    @Query("""
        SELECT s
        FROM BookableSlotEntity s
        WHERE s.status = :status
          AND (
                s.date < :currentDate
                OR (
                    s.date = :currentDate
                    AND s.endTime <= :currentTime
                )
              )
        """)
    List<BookableSlotEntity> findExpiredSlots(
            BookableSlotStatusEnum status,
            LocalDate currentDate,
            LocalTime currentTime
    );

}
