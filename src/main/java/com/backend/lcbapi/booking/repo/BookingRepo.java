package com.backend.lcbapi.booking.repo;

import com.backend.lcbapi.booking.entity.BookingEntity;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<BookingEntity, UUID> {


    @Query("""
            SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
            FROM BookingEntity b
            JOIN b.slot s
            WHERE b.student.id = :studentId
              AND b.status = :status
              AND s.date = :date
              AND s.startTime < :endTime
              AND s.endTime > :startTime
            """)
    boolean existsConflictingBooking(UUID studentId, LocalDate date, LocalTime startTime, LocalTime endTime, BookingStatusEnum status);

    boolean existsByStudent_IdAndSlot_AvailabilityWindow_IdAndStatus(UUID studentId, UUID availabilityWindowId, BookingStatusEnum status);



    @Query("""
        SELECT b
        FROM BookingEntity b
        JOIN FETCH b.slot s
        WHERE b.status = :status
          AND (
                s.date < :cutoffDate
                OR (
                    s.date = :cutoffDate
                    AND s.endTime <= :cutoffTime
                )
              )
        """)
    List<BookingEntity> findOutcomeExpiredBookings(
            BookingStatusEnum status,
            LocalDate cutoffDate,
            LocalTime cutoffTime
    );



}
