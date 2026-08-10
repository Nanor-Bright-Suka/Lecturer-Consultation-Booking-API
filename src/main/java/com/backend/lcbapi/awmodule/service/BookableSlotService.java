package com.backend.lcbapi.awmodule.service;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.awmodule.dto.response.BookableSlotResponseDto;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import com.backend.lcbapi.awmodule.mapper.BookableSlotMapper;
import com.backend.lcbapi.awmodule.repo.AvailabilityWindowRepo;
import com.backend.lcbapi.awmodule.repo.BookableSlotRepo;
import com.backend.lcbapi.shared.exceptions.InvalidCredentialException;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookableSlotService {

    private final AvailabilityWindowRepo availabilityWindowRepo;
    private final RoleContextService roleContextService;
    private final BookableSlotRepo bookableSlotRepo;
    private final BookableSlotMapper bookableSlotMapper;

   public List<BookableSlotEntity> generateSlots(AvailabilityWindowEntity availabilityWindow){

       List<BookableSlotEntity> slots = new ArrayList<>();

       LocalTime currentStart = availabilityWindow.getStartTime();
       LocalTime windowEnd = availabilityWindow.getEndTime();
       int slotDuration = availabilityWindow.getSlotDuration();

       while (!currentStart.plusMinutes(slotDuration).isAfter(windowEnd)) {

           LocalTime currentEnd = currentStart.plusMinutes(slotDuration);

           BookableSlotEntity slot = new BookableSlotEntity();
           slot.setId(UUID.randomUUID());
           slot.setDate(availabilityWindow.getDate());
           slot.setStartTime(currentStart);
           slot.setEndTime(currentEnd);
           slot.setStatus(BookableSlotStatusEnum.OPENED);
           slot.setAvailabilityWindow(availabilityWindow);
           slot.setCreatedAt(Instant.now());
           slot.setUpdatedAt(Instant.now());

           slots.add(slot);

           currentStart = currentEnd;
       }

       return slots;
   }


    @Transactional(readOnly = true)
    public List<BookableSlotResponseDto> getSlotsByAvailabilityWindow(UUID availabilityId) {

        LecturerEntity lecturer = roleContextService.getCurrentLecturer();

        AvailabilityWindowEntity availabilityWindow = availabilityWindowRepo.findById(availabilityId)
                .orElseThrow(() -> new NotFoundException("Availability window not found"));


        if (!availabilityWindow.getLecturer().getId().equals(lecturer.getId())) {
            throw new InvalidCredentialException("You do not have access to this availability window");
        }

        List<BookableSlotEntity> slots = bookableSlotRepo.findAllByAvailabilityWindowId(availabilityWindow.getId());

        return bookableSlotMapper.toDtoList(slots);
    }







}
