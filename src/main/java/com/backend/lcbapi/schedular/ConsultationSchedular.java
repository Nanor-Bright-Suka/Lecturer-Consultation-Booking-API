package com.backend.lcbapi.schedular;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsultationSchedular {

    private final Schedular schedular;


    @Scheduled(
            fixedDelayString = "${app.scheduler.availability-window-delay}"
    )
    public void processAvailabilityWindows() {

        log.debug("Processing availability windows");

        schedular.processAvailabilityWindows();
    }


    @Scheduled(
            fixedDelayString = "${app.scheduler.expired-slot-delay}"
    )
    public void processExpiredSlots() {

        log.debug("Processing expired bookable slots");

        schedular.processExpiredSlots();
    }


    @Scheduled(
            fixedDelayString = "${app.scheduler.outcome-delay}"
    )
    public void processExpiredOutcomes() {

        log.debug("Processing expired booking outcomes");

        schedular.processExpiredOutcomes();
    }













}
