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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
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
                    List.of(
                            BookableSlotStatusEnum.OPENED,
                            BookableSlotStatusEnum.BOOKED
                    ),

                            currentDate,
                            currentTime
                    );

            for (BookableSlotEntity slot : expiredSlots) {

                BookableSlotStatusEnum previousStatus =
                        slot.getStatus();

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
                if (previousStatus == BookableSlotStatusEnum.BOOKED) {
                    slot.getBookings()
                            .stream()
                            .filter(b ->
                                    b.getStatus() == BookingStatusEnum.SCHEDULED
                            )
                            .findFirst()
                            .ifPresentOrElse(
                                    booking ->
                                            booking.setStatus(BookingStatusEnum.AWAITING_MEETING_OUTCOME),
                                    () -> log.warn(
                                            "Expired BOOKED slot {} has no SCHEDULED booking",
                                            slot.getId()));
                }
            }
        }


//    THREE DAY TIMEOUT
@Transactional
public void processExpiredBookingOutcomes() {

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
