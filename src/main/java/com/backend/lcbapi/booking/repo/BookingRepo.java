package com.backend.lcbapi.booking.repo;

import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.booking.entity.BookingEntity;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<BookingEntity, UUID> {

    boolean existsByStudent_IdAndSlot_AvailabilityWindow_IdAndStatus(UUID studentId, UUID availabilityWindowId, BookingStatusEnum status);


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


    @Query("""
            SELECT b
            FROM BookingEntity b
            JOIN FETCH b.slot s
            JOIN FETCH s.availabilityWindow aw
            JOIN FETCH aw.lecturer l
            JOIN FETCH b.student st
            WHERE b.id = :bookingId
              AND st.id = :studentId
            ORDER BY s.date ASC, s.startTime ASC
            """)
    Optional<BookingEntity> findBookingForStudent(
             UUID bookingId,
             UUID studentId
    );

    @Query("""
            SELECT b
            FROM BookingEntity b
            JOIN FETCH b.slot s
            JOIN FETCH s.availabilityWindow aw
            JOIN FETCH aw.lecturer l
            JOIN FETCH b.student st
            WHERE b.id = :bookingId
              AND l.id = :lecturerId
            ORDER BY s.date ASC, s.startTime ASC
            """)
    Optional<BookingEntity> findBookingForLecturer(
             UUID bookingId,
             UUID lecturerId
    );

    @Query("""
            SELECT DISTINCT b
            FROM BookingEntity b
            JOIN FETCH b.student st
            JOIN FETCH b.slot s
            JOIN FETCH s.availabilityWindow aw
            JOIN FETCH aw.lecturer l
            JOIN FETCH l.user u
            WHERE st.id = :studentId
            ORDER BY s.date ASC, s.startTime ASC
            """)
    List<BookingEntity> findAllBookingsForStudent(
           UUID studentId
    );


    @Query("""
            SELECT DISTINCT b
            FROM BookingEntity b
            JOIN FETCH b.student st
            JOIN FETCH b.slot s
            JOIN FETCH s.availabilityWindow aw
            JOIN FETCH aw.lecturer l
            JOIN FETCH l.user u
            WHERE l.id = :lecturerId
            ORDER BY s.date ASC, s.startTime ASC
            """)
    List<BookingEntity> findAllBookingsForLecturer(
         UUID lecturerId
    );



    @Query("""
        SELECT b
        FROM BookingEntity b
        JOIN FETCH b.student s
        JOIN FETCH b.slot slot
        JOIN FETCH slot.availabilityWindow aw
        WHERE b.id = :bookingId
          AND s.id = :studentId
          AND aw.status <> :deletedStatus
        """)
    Optional<BookingEntity> findBookingForStudentCancellationAndStatusNot(
          UUID bookingId,
          UUID studentId,
          AvailabilityWindowStatusEnum deletedStatus
    );



    @Query("""
        SELECT b
        FROM BookingEntity b
        JOIN FETCH b.slot slot
        JOIN FETCH slot.availabilityWindow aw
        JOIN FETCH aw.lecturer l
        WHERE b.id = :bookingId
          AND l.id = :lecturerId
          AND aw.status <> :deletedStatus
        """)
    Optional<BookingEntity> findBookingForLecturerCancellationAndStatusNot(
            UUID bookingId,
            UUID lecturerId,
            AvailabilityWindowStatusEnum deletedStatus
    );













}
