package com.backend.lcbapi.booking.service;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.awmodule.service.RoleContextService;
import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.dto.response.booking.*;
import com.backend.lcbapi.booking.entity.BookingEntity;
import com.backend.lcbapi.booking.enums.BaseRoleEnum;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import com.backend.lcbapi.booking.mapper.BookingMapper;
import com.backend.lcbapi.booking.repo.BookingRepo;
import com.backend.lcbapi.shared.exceptions.ConflictException;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final BookableSlotRepo bookableSlotRepo;
    private final BookingMapper bookingMapper;
    private final RoleContextService roleContextService;

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















}
