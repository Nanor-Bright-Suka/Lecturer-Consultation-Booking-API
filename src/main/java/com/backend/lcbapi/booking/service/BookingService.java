package com.backend.lcbapi.booking.service;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.awmodule.service.RoleContextService;
import com.backend.lcbapi.booking.dto.request.consultation.BookingConsultationRequestDto;
import com.backend.lcbapi.booking.dto.request.consultation.CreateMeetingReportRequestDto;
import com.backend.lcbapi.booking.dto.request.consultation.ReviewMeetingReportRequestDto;
import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.dto.response.booking.*;
import com.backend.lcbapi.booking.dto.response.consultation.*;
import com.backend.lcbapi.booking.entity.BookingEntity;
import com.backend.lcbapi.booking.entity.MeetingReportEntity;
import com.backend.lcbapi.booking.enums.*;
import com.backend.lcbapi.booking.mapper.BookingMapper;
import com.backend.lcbapi.booking.mapper.MeetingReportMapper;
import com.backend.lcbapi.booking.repo.BookingRepo;
import com.backend.lcbapi.booking.repo.MeetingReportRepo;
import com.backend.lcbapi.shared.exceptions.ConflictException;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;


import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final BookableSlotRepo bookableSlotRepo;
    private final BookingMapper bookingMapper;
    private final RoleContextService roleContextService;
    private final MeetingReportRepo meetingReportRepo;
    private final MeetingReportMapper meetingReportMapper;

    private final Clock clock;


    @Transactional
    public CreateBookingResponseDto createBookingService(UUID slotId) {

        StudentEntity student = roleContextService.getCurrentStudent();

        // 2. Get and lock the selected slot
        BookableSlotEntity slot = bookableSlotRepo.findByIdForUpdateAndStatusNot(slotId, AvailabilityWindowStatusEnum.DELETED)
                        .orElseThrow(() -> new NotFoundException("Bookable slot not found"));

        // 3. Slot must be AVAILABLE
        validateSlotAvailability(slot);

        // 4. Availability window must not have started
        validateAvailabilityWindow(slot);

        // 5. Slot must not have expired
        validateSlotExpiration(slot);

        // 6. Student must not already have
        //    a booking in this availability window
        validateExistingWindowBooking(student.getId(), slot);

        // 7. Student must not have a conflicting booking
        validateBookingConflict(student.getId(), slot);

        LocalDateTime now = LocalDateTime.now(clock);

        // 8. Create booking
        BookingEntity booking = BookingEntity.builder()
                .id(UUID.randomUUID())
                .student(student)
                .slot(slot)
                .status(BookingStatusEnum.SCHEDULED)
                .bookedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 9. Update slot status
        slot.setStatus(BookableSlotStatusEnum.BOOKED);

        // 10. Save booking
        BookingEntity savedBooking = bookingRepo.save(booking);

        // 11. Map to response
        return bookingMapper.toResponseDto(savedBooking);
    }


    private void validateSlotAvailability(BookableSlotEntity slot) {

        if (slot.getStatus() != BookableSlotStatusEnum.OPENED) {

            throw new ConflictException("Bookable slot is not available");
        }
    }


    private void validateAvailabilityWindow(BookableSlotEntity slot) {

        var window = slot.getAvailabilityWindow();

        LocalDateTime windowStart = LocalDateTime.of(
                window.getDate(),
                window.getStartTime()
        );

        LocalDateTime now = LocalDateTime.now(clock);

        if (!now.isBefore(windowStart)) {
            throw new ConflictException("Booking is no longer allowed because the availability window is in progress");
        }
    }


    private void validateSlotExpiration(BookableSlotEntity slot) {

        LocalDateTime slotEnd = LocalDateTime.of(
                slot.getDate(),
                slot.getEndTime()
        );

        LocalDateTime now = LocalDateTime.now(clock);

        if (!now.isBefore(slotEnd)) {
            throw new ConflictException("Bookable slot has expired");
        }
    }


    private void validateExistingWindowBooking(UUID studentId, BookableSlotEntity slot) {

        UUID availabilityWindowId = slot.getAvailabilityWindow().getId();

        boolean alreadyBooked = bookingRepo.existsByStudent_IdAndSlot_AvailabilityWindow_IdAndStatus(
                                studentId,
                                availabilityWindowId,
                                BookingStatusEnum.SCHEDULED
                        );

        if (alreadyBooked) {
            throw new ConflictException("You have already booked a slot in this availability window");
        }

    }


    private void validateBookingConflict(UUID studentId, BookableSlotEntity slot) {

        boolean conflict = bookingRepo.existsConflictingBooking(
                        studentId,
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        BookingStatusEnum.SCHEDULED
                );

        if (conflict) {
            throw new ConflictException("You already have a booking that conflicts with this slot");
        }
    }



    @Transactional(readOnly = true)
    public StudentBookingResponseDto getStudentBooking(UUID bookingId) {

        StudentEntity student = roleContextService.getCurrentStudent();

        BookingEntity booking =
                bookingRepo.findBookingForStudent(bookingId, student.getId()).orElseThrow(() ->
                        new NotFoundException("Booking not found")
                );

        return bookingMapper.toStudentResponseView(booking);
    }



    @Transactional(readOnly = true)
    public LecturerBookingViewDto getLecturerBooking(UUID bookingId) {

        LecturerEntity lecturer =
                roleContextService.getCurrentLecturer();


        BookingEntity booking =
                        bookingRepo
                        .findBookingForLecturer(bookingId, lecturer.getId())
                        .orElseThrow(() ->
                        new NotFoundException("Booking not found")
                         );

        return bookingMapper
                .toLecturerBookingResponseView(booking);
    }




    @Transactional(readOnly = true)
    public List<StudentBookingSummaryDto> getAllStudentBookings() {

        StudentEntity student =
                roleContextService.getCurrentStudent();

        return bookingRepo
                .findAllBookingsForStudent(student.getId())
                .stream()
                .map(bookingMapper::toStudentSummary)
                .toList();
    }





    @Transactional(readOnly = true)
    public List<LecturerBookingSummaryDto> getAllLecturerBookings() {

        LecturerEntity lecturer =
                roleContextService.getCurrentLecturer();


        return bookingRepo
                .findAllBookingsForLecturer(lecturer.getId())
                .stream()
                .map(bookingMapper::toLecturerSummary)
                .toList();
    }






    @Transactional
    public CancelBookingResponseDto cancelBookingByStudent(UUID bookingId) {

        StudentEntity student =
                roleContextService.getCurrentStudent();

        BookingEntity booking =
                        bookingRepo
                        .findBookingForStudentCancellationAndStatusNot(
                                bookingId,
                                student.getId(),
                                AvailabilityWindowStatusEnum.DELETED
                        )
                        .orElseThrow(() -> new NotFoundException(
                                "Booking not found"));

        /*
         * 1. Booking must be SCHEDULED
         */
        if (booking.getStatus() != BookingStatusEnum.SCHEDULED) {
            throw new ConflictException(
                    "Booking cannot be cancelled in its current state");
        }

        BookableSlotEntity slot =
                                booking.getSlot();

        AvailabilityWindowEntity window =
                                  slot.getAvailabilityWindow();

        /*
         * 2. Slot must currently be BOOKED
         */
        if (slot.getStatus() != BookableSlotStatusEnum.BOOKED) {
            throw new ConflictException(
                    "Booking cannot be cancelled because the slot is no longer booked"
            );
        }

        /*
         * 3. Availability Window must not have started
         */
        LocalDateTime windowStart =
                                    LocalDateTime.of(
                                            window.getDate(),
                                            window.getStartTime()
                                    );


        LocalDateTime now = LocalDateTime.now(clock);
        /*
              * 4. Cancel booking
         */


        booking.setStatus(BookingStatusEnum.CANCELLED);

        booking.setCancelledAt(now);

        booking.setCancelledBy(BaseRoleEnum.STUDENT);



        /*
         * Slot transition depends on whether
         * the availability window has started.
         */
        if (now.isBefore(windowStart)) {

            // Case 1:
            // Window has NOT started.
            // Release slot for another student.

            slot.setStatus(BookableSlotStatusEnum.OPENED);

        } else {

            // Case 2:
            // Window has started.
            // Slot is permanently cancelled.

            slot.setStatus(BookableSlotStatusEnum.CANCELLED);
        }



        /*
         * 6. Save
         */
        bookingRepo.save(booking);

        /*
         * slot is managed by the same transaction,
         * so its change will be persisted as well.
         */

        return bookingMapper.toCancelBookingResponse(booking);
    }


    @Transactional
    public CancelBookingResponseDto cancelBookingByLecturer(UUID bookingId) {


        LecturerEntity lecturer =
                roleContextService.getCurrentLecturer();

        BookingEntity booking =
                         bookingRepo.findBookingForLecturerCancellationAndStatusNot(
                                bookingId,
                                lecturer.getId(),
                                 AvailabilityWindowStatusEnum.DELETED
                        )
                        .orElseThrow(() -> new NotFoundException("Booking not found"));

        /*
         * Booking must be SCHEDULED.
         */
        if (booking.getStatus() != BookingStatusEnum.SCHEDULED) {
            throw new ConflictException(
                    "Booking cannot be cancelled in its current state"
            );
        }

        BookableSlotEntity slot =
                             booking.getSlot();

        /*
         * Slot must be BOOKED.
         */
        if (slot.getStatus() != BookableSlotStatusEnum.BOOKED) {
            throw new ConflictException(
                    "Booking cannot be cancelled because the slot is no longer booked"
            );
        }


        LocalDateTime now = LocalDateTime.now(clock);

        /*
         * Cancel booking.
         */

        booking.setStatus(BookingStatusEnum.CANCELLED);

        booking.setCancelledAt(now);

        booking.setCancelledBy(BaseRoleEnum.LECTURER);

        /*
         * Cancel slot permanently.
         */
        slot.setStatus(BookableSlotStatusEnum.CANCELLED);

        bookingRepo.save(booking);

        return bookingMapper.toCancelBookingResponse(booking);
    }





    @Transactional
    public ConsultationOutcomeResponseDto recordConsultationOutcome(
            UUID bookingId,
            BookingConsultationRequestDto request
    ) {


        LecturerEntity lecturer =
                roleContextService.getCurrentLecturer();

        BookingEntity booking =
                               bookingRepo.findBookingForLecturer(
                                bookingId,
                                lecturer.getId())
                        .orElseThrow(() -> new NotFoundException("Booking not found")
                        );

        /*
         * The lecturer can only record an outcome
         * after the consultation period has ended
         * and the scheduler has moved the booking
         * into AWAITING_MEETING_OUTCOME.
         */
        if (booking.getStatus()
                != BookingStatusEnum.AWAITING_MEETING_OUTCOME) {

            throw new ConflictException(
                    "Consultation outcome cannot be recorded for a booking in its current state"
            );
        }

        AttendanceStatus.AttendanceStatusEnum attendanceStatus =
                                                 request.getAttendanceStatus();

        /*
         * Lecturer is only allowed to record:
         *
         * BOTH_ATTENDED
         * STUDENT_NO_SHOW
         */
        Set<AttendanceStatus.AttendanceStatusEnum> validStatuses = EnumSet.of(
                AttendanceStatus.AttendanceStatusEnum.BOTH_ATTENDED,
                AttendanceStatus.AttendanceStatusEnum.STUDENT_NO_SHOW
        );

        if (!validStatuses.contains(attendanceStatus)) {
            throw new ConflictException(
                    "Invalid attendance status for lecturer consultation outcome"
            );
        }
        /*
         * Record attendance.
         */
        booking.setAttendanceStatus(attendanceStatus);

        /*
         * Determine the booking's final status.
         */
        if (attendanceStatus == AttendanceStatus.AttendanceStatusEnum.BOTH_ATTENDED) {

            booking.setStatus(BookingStatusEnum.COMPLETED);

        } else {

            booking.setStatus(BookingStatusEnum.NO_SHOW);
        }

        /*
         * Record when the consultation outcome
         * was finalized.
         */
        booking.setCompletedAt(
                LocalDateTime.now(clock)
        );

        return new ConsultationOutcomeResponseDto(
                booking.getId(),
                booking.getStatus(),
                booking.getAttendanceStatus(),
                booking.getCompletedAt()
        );
    }






    @Transactional
    public MeetingReportResponseDto createMeetingReport(
            UUID bookingId,
            CreateMeetingReportRequestDto request
    ) {


        StudentEntity student =
                roleContextService.getCurrentStudent();


        /*
         * Find the booking and make sure it belongs
         * to this student.
         */
        BookingEntity booking =
                                bookingRepo.findBookingForStudent(
                                bookingId,
                                student.getId())
                        .orElseThrow(() -> new NotFoundException("Booking not found")
                        );

        /*
         * A report can only be submitted while the
         * system is waiting for the consultation outcome.
         */
        if (booking.getStatus() != BookingStatusEnum.AWAITING_MEETING_OUTCOME) {

            throw new ConflictException(
                    "A meeting report cannot be submitted for this booking in its current state"
            );
        }

        /*
         * A booking can only have one report.
         */
        if (meetingReportRepo.existsByBookingId(booking.getId())) {

            throw new ConflictException(
                    "A meeting report has already been submitted for this booking"
            );
        }

        LocalDateTime now = LocalDateTime.now(clock);
        /*
         * Create the report.
         */
        MeetingReportEntity report =
                                    MeetingReportEntity.builder()
                                            .id(UUID.randomUUID())
                                            .booking(booking)
                                            .description(request.getDescription().trim())
                                            .status(MeetingReportStatusEnum.PENDING)
                                            .createdAt(now)
                                            .updatedAt(now)
                                            .build();

        /*
         * Move booking into administrative review.
         */
        booking.setStatus(BookingStatusEnum.PENDING_APPROVAL);
        booking.setAttendanceStatus(AttendanceStatus.AttendanceStatusEnum.PENDING_APPROVAL);

        meetingReportRepo.save(report);

        /*
         * Booking is managed by the current transaction,
         * so an explicit save is normally not necessary.
         *
         * If you prefer explicit persistence in your service,
         * you can call bookingRepository.save(booking).
         */

        return new MeetingReportResponseDto(
                report.getId(),
                report.getStatus(),
                report.getBooking().getStatus(),
                report.getCreatedAt()

        );
    }




    @Transactional
    public MeetingReportReviewResponseDto reviewMeetingReport(
            UUID reportId,
            ReviewMeetingReportRequestDto request
    ) {

        MeetingReportEntity report =
                                        meetingReportRepo
                                                .findBookingById(reportId)
                                                .orElseThrow(() -> new NotFoundException("Meeting report not found")
                                                );

        /*
         * A report can only be reviewed once.
         */
        if (report.getStatus() != MeetingReportStatusEnum.PENDING) {

            throw new ConflictException(
                    "Meeting report has already been reviewed"
            );
        }

        BookingEntity booking = report.getBooking();

        /*
         * Keep the report and booking lifecycle
         * synchronized.
         */
        if (booking.getStatus() != BookingStatusEnum.PENDING_APPROVAL) {

            throw new ConflictException(
                    "Booking is not awaiting administrative approval"
            );
        }

        /*
         * Record who reviewed the report.
         */


       LocalDateTime now = LocalDateTime.now(clock);

        report.setReviewedAt(now);
        report.setUpdatedAt(now);

        if (request.reason() != null) {
            report.setReason(request.reason().trim());
        }

        /*
         * ADMIN APPROVES STUDENT'S CLAIM
         *
         * Lecturer was determined to be absent.
         */
        if (request.decision() == MeetingReportDecisionEnum.APPROVED) {

            report.setStatus(MeetingReportStatusEnum.APPROVED);

            booking.setStatus(BookingStatusEnum.NO_SHOW);

            booking.setAttendanceStatus(AttendanceStatus.AttendanceStatusEnum.LECTURER_NO_SHOW);

        }

        /*
         * ADMIN REJECTS STUDENT'S CLAIM
         *
         * Lecturer absence was not confirmed.
         */
        else {

            report.setStatus(MeetingReportStatusEnum.REJECTED);

            booking.setStatus(BookingStatusEnum.COMPLETED);

            booking.setAttendanceStatus(AttendanceStatus.AttendanceStatusEnum.BOTH_ATTENDED);
        }

        return new MeetingReportReviewResponseDto(
                report.getId(),
                report.getStatus(),
                booking.getStatus(),
                booking.getAttendanceStatus(),
                report.getReviewedAt()


        );
    }




    public List<MeetingReportSummaryResponseDto> getMeetingReports(MeetingReportStatusEnum status) {

        return meetingReportRepo.findAllMeetingReportsForAdmin(status);
    }



        @Transactional(readOnly = true)
        public MeetingReportDetailResponse getMeetingReport(UUID reportId) {

            MeetingReportEntity report =
                                            meetingReportRepo
                                            .findByIdWithDetails(reportId)
                                            .orElseThrow(() -> new NotFoundException("Meeting report not found"));

            return meetingReportMapper.toDetailResponse(report);
        }





















}
