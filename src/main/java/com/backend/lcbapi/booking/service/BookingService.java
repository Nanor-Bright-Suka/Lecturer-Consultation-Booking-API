package com.backend.lcbapi.booking.service;


import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.auth.repo.StudentRepository;
import com.backend.lcbapi.auth.service.AuthenticatedUserService;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.awmodule.service.RoleContextService;
import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.entity.BookingEntity;
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
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final BookableSlotRepo bookableSlotRepo;
    private final StudentRepository studentRepo;
    private final BookingMapper bookingMapper;
    private final AuthenticatedUserService authenticatedUserService;
    private final RoleContextService roleContextService;

    private final Clock clock;


    @Transactional
    public CreateBookingResponseDto createBookingService(UUID slotId) {

        StudentEntity student = roleContextService.getCurrentStudent();

        // 2. Get and lock the selected slot
        BookableSlotEntity slot = bookableSlotRepo.findById(slotId)
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

        if (slot.getStatus() != BookableSlotStatusEnum.AVAILABLE) {

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
            throw new ConflictException("Booking is no longer allowed because the availability window has started");
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











}
