package com.backend.lcbapi.booking.repo;



import com.backend.lcbapi.booking.dto.response.consultation.MeetingReportSummaryResponseDto;
import com.backend.lcbapi.booking.entity.MeetingReportEntity;
import com.backend.lcbapi.booking.enums.MeetingReportStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeetingReportRepo extends JpaRepository<MeetingReportEntity, UUID> {

    boolean existsByBookingId(UUID bookingId);


    @Query("""
            SELECT r
            FROM MeetingReportEntity r
            JOIN FETCH r.booking b
            WHERE r.id = :reportId
            """)
    Optional<MeetingReportEntity> findBookingById(
           UUID reportId
    );






    @Query("""
            SELECT new com.backend.lcbapi.booking.dto.response.consultation.MeetingReportSummaryResponseDto(
                r.id,
                CONCAT(s.user.firstName, ' ', s.user.lastName),
                CONCAT(l.user.firstName, ' ', l.user.lastName),
                r.status,
                r.createdAt
            )
            FROM MeetingReportEntity r
            JOIN r.booking b
            JOIN b.student s
            JOIN b.slot slot
            JOIN slot.availabilityWindow aw
            JOIN aw.lecturer l
            WHERE (:status IS NULL OR r.status = :status)
            ORDER BY r.createdAt DESC
            """)
    List<MeetingReportSummaryResponseDto> findAllMeetingReportsForAdmin(
       MeetingReportStatusEnum status
    );





    @Query("""
        SELECT r
        FROM MeetingReportEntity r
        JOIN FETCH r.booking b
        JOIN FETCH b.student s
        JOIN FETCH s.user su
        JOIN FETCH b.slot slot
        JOIN FETCH slot.availabilityWindow aw
        JOIN FETCH aw.lecturer l
        JOIN FETCH l.user lu
        WHERE r.id = :reportId
        """)
    Optional<MeetingReportEntity> findByIdWithDetails(
        UUID reportId
    );


}
