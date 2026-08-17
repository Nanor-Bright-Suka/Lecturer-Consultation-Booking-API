package com.backend.lcbapi.awmodule.repo;

import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookableSlotRepo extends JpaRepository<BookableSlotEntity, UUID> {
    long countByAvailabilityWindowId(UUID availabilityWindowId);
    void deleteAllByAvailabilityWindowIdAndStatus(UUID availabilityWindowId, BookableSlotStatusEnum status);
    boolean existsByAvailabilityWindowIdAndStatusNot(UUID availabilityWindowId, BookableSlotStatusEnum status);
    void deleteByAvailabilityWindowLecturerId(UUID lecturerId);



    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM BookableSlotEntity s
    JOIN s.bookings b
    WHERE s.availabilityWindow.id = :availabilityWindowId
      AND s.status = :slotStatus
      AND b.status IN :bookingStatuses
    """)
    boolean existsActiveBooking(
            UUID availabilityWindowId,
            BookableSlotStatusEnum slotStatus,
          Collection<BookingStatusEnum> bookingStatuses
    );


    @Query("""
    SELECT s
    FROM BookableSlotEntity s
    JOIN s.availabilityWindow aw
    WHERE aw.id = :availabilityWindowId
      AND aw.status <> :deletedStatus
    ORDER BY s.date ASC, s.startTime ASC
    """)
    List<BookableSlotEntity> findAllByAvailabilityWindowId(
           UUID availabilityWindowId,
             AvailabilityWindowStatusEnum deletedStatus
    );

    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
       FROM BookableSlotEntity s
       JOIN s.availabilityWindow aw
       WHERE s.id = :slotId
         AND aw.status <> :deletedStatus
    """)
    Optional<BookableSlotEntity> findByIdForUpdateAndStatusNot(
            UUID slotId,
            AvailabilityWindowStatusEnum deletedStatus);


    @Query("""
        SELECT s
        FROM BookableSlotEntity s
        WHERE s.status IN :statuses
          AND (
                s.date < :currentDate
                OR (
                    s.date = :currentDate
                    AND s.endTime <= :currentTime
                )
              )
        """)
    List<BookableSlotEntity> findExpiredSlots(
           Collection<BookableSlotStatusEnum> statuses,
            LocalDate currentDate,
            LocalTime currentTime
    );


    @Query("""
        SELECT s
        FROM BookableSlotEntity s
        JOIN FETCH s.availabilityWindow aw
        JOIN FETCH aw.lecturer l
        LEFT JOIN FETCH s.bookings b
        WHERE s.id = :slotId
          AND l.id = :lecturerId
          AND aw.status <> :deletedStatus
        """)
    Optional<BookableSlotEntity> findSlotForLecturerCancellation(
           UUID slotId,
            UUID lecturerId,
           AvailabilityWindowStatusEnum deletedStatus
    );

}
