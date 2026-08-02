package com.backend.lcbapi.awmodule.service;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.repo.LecturerRepository;
import com.backend.lcbapi.auth.service.AuthenticatedUserService;
import com.backend.lcbapi.awmodule.dto.request.CreateAvailabilityWindowRequestDto;
import com.backend.lcbapi.awmodule.dto.request.UpdateAvailabilityRequestDto;
import com.backend.lcbapi.awmodule.dto.response.AvailabilityWindowResponseDto;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityModeEnum;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.awmodule.mapper.AvailabilityWindowMapper;
import com.backend.lcbapi.awmodule.repo.AvailabilityWindowRepo;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.shared.exceptions.ForbiddenException;
import com.backend.lcbapi.shared.exceptions.InvalidCredentialException;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import com.backend.lcbapi.shared.exceptions.ResourceAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityWindowService {

    private final Clock clock;
    private final LecturerServiceContext lecturerServiceContext;
    private final AvailabilityWindowRepo availabilityWindowRepo;
    private final AvailabilityWindowMapper availabilityWindowMapper;
    private final BookableSlotService bookableSlotService;
    private final BookableSlotRepo bookableSlotRepo;

    @Transactional
    public AvailabilityWindowResponseDto createAvailabilityWindow(CreateAvailabilityWindowRequestDto request) {

        validateAvailabilityWindow(request);

        validateModeRequirements(request);

        LecturerEntity loggedInLecturer = lecturerServiceContext.getCurrentLecturer();

        boolean exists = availabilityWindowRepo.existsConflict(loggedInLecturer.getId(), request.getDate(), request.getStartTime(), request.getEndTime());

        if (exists) {
            throw new ResourceAlreadyExistException("You already have an availability window during this time");
        }


        AvailabilityWindowEntity availabilityWindow = availabilityWindowMapper.toEntity(request);

        availabilityWindow.setLecturer(loggedInLecturer);

        AvailabilityWindowEntity savedWindow = availabilityWindowRepo.save(availabilityWindow);


        List<BookableSlotEntity> slots = bookableSlotService.generateSlots(savedWindow);

        bookableSlotRepo.saveAll(slots);

        AvailabilityWindowResponseDto response = availabilityWindowMapper.toDto(savedWindow);
        response.setSlotsGenerated(slots.size());

        return response;

    }


    private void validateAvailabilityWindow(CreateAvailabilityWindowRequestDto request) {

        if (request.getDate().isBefore(LocalDate.now(clock))) {
            throw new InvalidCredentialException("Cannot create availability for a past date");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new InvalidCredentialException("Start time must be before end time");
        }


        if (request.getSlotDuration() <= 0) {
            throw new InvalidCredentialException("Slot duration must be greater than zero");
        }

        long totalMinutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        if (request.getSlotDuration() > totalMinutes) {
            throw new InvalidCredentialException("Slot duration cannot exceed availability window duration");
        }
    }


    private void validateModeRequirements(CreateAvailabilityWindowRequestDto request) {

        AvailabilityModeEnum mode = request.getMode();


        switch (mode) {

            case IN_PERSON -> {

                requireField(request.getVenue(), "Venue is required for IN_PERSON consultation");

                rejectField(request.getMeetingLink(), "Meeting link is not allowed for IN_PERSON consultation");

                rejectField(request.getCallInstruction(), "Call instruction is not allowed for IN_PERSON consultation");
            }


            case ONLINE -> {

                requireField(request.getMeetingLink(), "Meeting link is required for ONLINE consultation");

                rejectField(request.getVenue(), "Venue is not allowed for ONLINE consultation");

                rejectField(request.getCallInstruction(), "Call instruction is not allowed for ONLINE consultation");
            }


            case PHONE_CALL -> {

                requireField(request.getCallInstruction(), "Call instruction is required for PHONE_CALL consultation");

                rejectField(request.getVenue(), "Venue is not allowed for PHONE_CALL consultation");

                rejectField(request.getMeetingLink(), "Meeting link is not allowed for PHONE_CALL consultation");
            }
        }
    }


    private void requireField(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidCredentialException(message);
        }
    }


    private void rejectField(String value, String message) {
        if (value != null && !value.isBlank()) {
            throw new InvalidCredentialException(message);
        }
    }


    @Transactional(readOnly = true)
    public List<AvailabilityWindowResponseDto> getMyAvailabilityWindowsService() {
        LecturerEntity lecturer = lecturerServiceContext.getCurrentLecturer();

        List<AvailabilityWindowEntity> windows = availabilityWindowRepo.findAllByLecturerIdAndStatus(lecturer.getId(), AvailabilityWindowStatusEnum.ACTIVE);

        return windows.stream()
                .map(window -> {
                    AvailabilityWindowResponseDto dto = availabilityWindowMapper.toDto(window);
                    long generatedSlots = bookableSlotRepo.countByAvailabilityWindowId(window.getId());
                    dto.setSlotsGenerated((int) generatedSlots);

                    return dto;
                })
                .toList();
    }



    @Transactional
    public AvailabilityWindowResponseDto updateAvailabilityWindow(UUID availabilityId, UpdateAvailabilityRequestDto request) {

        LecturerEntity lecturer = lecturerServiceContext.getCurrentLecturer();

        AvailabilityWindowEntity window = availabilityWindowRepo.findByIdAndStatus(availabilityId, AvailabilityWindowStatusEnum.ACTIVE)
                        .orElseThrow(() -> new NotFoundException("Availability window not found"));

        if (!window.getLecturer().getId().equals(lecturer.getId())) {
            throw new ForbiddenException("You cannot update this availability window");
        }

        boolean hasProcessedSlots = bookableSlotRepo.existsByAvailabilityWindowIdAndStatusNot(availabilityId, BookableSlotStatusEnum.AVAILABLE);

        if (request.getMode() != null) {
            validateModeFields(request.getMode(), request.getVenue(), request.getMeetingLink(), request.getCallInstruction());
        }

        if (hasProcessedSlots) {
            validateMetadataOnly(request);
            updateMetadata(window, request);

        } else {
            updateFullWindow(window, request);

            bookableSlotRepo.deleteAllByAvailabilityWindowIdAndStatus(availabilityId, BookableSlotStatusEnum.AVAILABLE);

            List<BookableSlotEntity> slots = bookableSlotService.generateSlots(window);

            bookableSlotRepo.saveAll(slots);
        }

        validateModeFields(window.getMode(), window.getVenue(), window.getMeetingLink(), window.getCallInstruction());

        AvailabilityWindowEntity saved = availabilityWindowRepo.save(window);
        AvailabilityWindowResponseDto dto = availabilityWindowMapper.toDto(saved);

        long generatedSlots = bookableSlotRepo.countByAvailabilityWindowId(window.getId());

        dto.setSlotsGenerated((int) generatedSlots);
        return dto;
    }



    private void validateMetadataOnly(UpdateAvailabilityRequestDto request) {

        if(request.getDate() != null ||
                request.getStartTime() != null ||
                request.getEndTime() != null ||
                request.getSlotDuration() != null) {

            throw new InvalidCredentialException("Cannot modify date, time or duration because slots have already been processed");
        }

    }


    private void updateMetadata(AvailabilityWindowEntity window, UpdateAvailabilityRequestDto request) {
        if(request.getMode() != null) {
            window.setMode(request.getMode());

            switch(request.getMode()) {

                case ONLINE -> {
                    window.setMeetingLink(request.getMeetingLink());
                    window.setVenue(null);
                    window.setCallInstruction(null);
                }

                case IN_PERSON -> {
                    window.setVenue(request.getVenue());
                    window.setMeetingLink(null);
                    window.setCallInstruction(null);
                }

                case PHONE_CALL -> {
                    window.setCallInstruction(request.getCallInstruction());
                    window.setVenue(null);
                    window.setMeetingLink(null);
                }
            }

            return;
        }

        if(request.getVenue() != null)
            window.setVenue(request.getVenue());

        if(request.getMeetingLink() != null)
            window.setMeetingLink(request.getMeetingLink());

        if(request.getCallInstruction() != null)
            window.setCallInstruction(request.getCallInstruction());

    }



    private void updateFullWindow(AvailabilityWindowEntity window, UpdateAvailabilityRequestDto request) {

        if(request.getDate() != null)
            window.setDate(request.getDate());

        if(request.getStartTime() != null)
            window.setStartTime(request.getStartTime());

        if(request.getEndTime() != null)
            window.setEndTime(request.getEndTime());

        if(request.getSlotDuration() != null)
            window.setSlotDuration(request.getSlotDuration());

        updateMetadata(window, request);

    }


    private void validateModeFields(AvailabilityModeEnum mode, String venue, String meetingLink, String callInstruction) {

        switch(mode) {

            case ONLINE -> {

                if(!StringUtils.hasText(meetingLink)) {
                    throw new InvalidCredentialException("Meeting link is required for online mode");
                }

                if(StringUtils.hasText(venue) || StringUtils.hasText(callInstruction)) {
                    throw new InvalidCredentialException("Invalid fields supplied for online mode");
                }
            }


            case IN_PERSON -> {

                if(!StringUtils.hasText(venue)) {
                    throw new InvalidCredentialException("Venue is required for in-person mode");
                }

                if(StringUtils.hasText(meetingLink) || StringUtils.hasText(callInstruction)) {
                    throw new InvalidCredentialException("Invalid fields supplied for in-person mode");
                }
            }


            case PHONE_CALL -> {

                if(!StringUtils.hasText(callInstruction)) {
                    throw new InvalidCredentialException("Call instruction required for phone call");
                }

                if(StringUtils.hasText(meetingLink) || StringUtils.hasText(venue)) {
                    throw new InvalidCredentialException("Invalid fields supplied for phone call mode");
                }
            }
        }
    }



    @Transactional
    public void deleteAvailabilityWindow(UUID availabilityId) {

        LecturerEntity lecturer = lecturerServiceContext.getCurrentLecturer();


        AvailabilityWindowEntity window = availabilityWindowRepo.findByIdAndStatus(availabilityId, AvailabilityWindowStatusEnum.ACTIVE)
                        .orElseThrow(() -> new NotFoundException("Availability window not found"));


        if (!window.getLecturer().getId().equals(lecturer.getId())) {
            throw new ForbiddenException("You cannot delete this availability window");
        }


        boolean hasProcessedSlots = bookableSlotRepo.existsByAvailabilityWindowIdAndStatusNot(availabilityId, BookableSlotStatusEnum.AVAILABLE);

        if (hasProcessedSlots) {
            throw new InvalidCredentialException("Cannot delete availability window because slots have already been processed");
        }

        window.setStatus(AvailabilityWindowStatusEnum.DELETED);

        availabilityWindowRepo.save(window);
    }
























}
