package com.backend.lcbapi.schedular;


import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.awmodule.repo.AvailabilityWindowRepo;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.booking.entity.BookingEntity;
import com.backend.lcbapi.booking.enums.AttendanceStatusEnum;
import com.backend.lcbapi.booking.enums.BookingStatusEnum;
import com.backend.lcbapi.booking.repo.BookingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;


@Service
@RequiredArgsConstructor
public class Schedular {


    private final Clock clock;

    private final AvailabilityWindowRepo availabilityWindowRepo;
    private final BookableSlotRepo bookableSlotRepo;
    private final BookingRepo bookingRepo;


    @Transactional
    public void processAvailabilityWindows() {

        LocalDateTime now = LocalDateTime.now(clock)
                .atZone(clock.getZone())
                .toLocalDateTime();

        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        /*
         * ACTIVE → IN_PROGRESS
         */
        List<AvailabilityWindowEntity> windowsToStart =
                availabilityWindowRepo.findWindowsReadyToStart(
                        AvailabilityWindowStatusEnum.ACTIVE,
                        currentDate,
                        currentTime
                );

        for (AvailabilityWindowEntity window : windowsToStart) {
            window.setStatus(AvailabilityWindowStatusEnum.IN_PROGRESS);
        }


        /*
         * IN_PROGRESS → INACTIVE
         */
        List<AvailabilityWindowEntity> windowsToEnd =
                availabilityWindowRepo.findWindowsReadyToEnd(
                        AvailabilityWindowStatusEnum.IN_PROGRESS,
                        currentDate,
                        currentTime
                );

        for (AvailabilityWindowEntity window : windowsToEnd) {
            window.setStatus(AvailabilityWindowStatusEnum.INACTIVE);
        }
    }



// SLOT PROCESSING

        @Transactional
        public void processExpiredSlots() {

            LocalDateTime now =
                    LocalDateTime.now(clock)
                            .atZone(clock.getZone())
                            .toLocalDateTime();

            LocalDate currentDate = now.toLocalDate();
            LocalTime currentTime = now.toLocalTime();

            List<BookableSlotEntity> expiredSlots = bookableSlotRepo.findExpiredSlots(
                            BookableSlotStatusEnum.BOOKED,
                            currentDate,
                            currentTime
                    );

            for (BookableSlotEntity slot : expiredSlots) {

                /*
                 * Slot lifecycle
                 *
                 * BOOKED → CLOSED
                 */
                slot.setStatus(BookableSlotStatusEnum.CLOSED);

                /*
                 * Booking lifecycle
                 *
                 * SCHEDULED → AWAITING_OUTCOME
                 */
                BookingEntity booking = slot.getBooking();

                if (booking != null &&
                        booking.getStatus() ==
                                BookingStatusEnum.SCHEDULED) {

                    booking.setStatus(BookingStatusEnum.AWAITING_MEETING_OUTCOME);
                }
            }
        }


//    THREE DAY TIMEOUT
@Transactional
public void processExpiredOutcomes() {

    LocalDateTime now = LocalDateTime.now(clock)
                    .atZone(clock.getZone())
                    .toLocalDateTime();

    LocalDateTime outcomeDeadline = now.minusDays(3);

    LocalDate cutoffDate = outcomeDeadline.toLocalDate();

    LocalTime cutoffTime = outcomeDeadline.toLocalTime();

    List<BookingEntity> expiredBookings = bookingRepo.findOutcomeExpiredBookings(
                    BookingStatusEnum.AWAITING_MEETING_OUTCOME,
                    cutoffDate,
                    cutoffTime
            );

    for (BookingEntity booking : expiredBookings) {

        /*
         * No outcome was recorded within 3 days.
         */
        booking.setAttendanceStatus(AttendanceStatusEnum.BOTH_ABSENT);

        booking.setStatus(BookingStatusEnum.COMPLETED);

        booking.setCompletedAt(LocalDateTime.now(clock));
    }






}




















}
