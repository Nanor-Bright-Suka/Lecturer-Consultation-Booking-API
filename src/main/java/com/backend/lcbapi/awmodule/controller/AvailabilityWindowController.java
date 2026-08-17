package com.backend.lcbapi.awmodule.controller;


import com.backend.lcbapi.auth.dto.response.ApiResponse;
import com.backend.lcbapi.awmodule.dto.request.CreateAvailabilityWindowRequestDto;
import com.backend.lcbapi.awmodule.dto.request.UpdateAvailabilityRequestDto;
import com.backend.lcbapi.awmodule.dto.response.AvailabilityWindowResponseDto;
import com.backend.lcbapi.awmodule.dto.response.BookableSlotResponseDto;
import com.backend.lcbapi.awmodule.service.AvailabilityWindowService;
import com.backend.lcbapi.awmodule.service.BookableSlotService;
import com.backend.lcbapi.booking.dto.response.ApiResponseDto;
import com.backend.lcbapi.booking.dto.response.booking.CancelSlotResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityWindowController {


    private final AvailabilityWindowService availabilityWindowService;
    private final BookableSlotService bookableSlotService;


    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_AVAILABILITY_WINDOW')")
    public ResponseEntity<AvailabilityWindowResponseDto> create(@Valid @RequestBody CreateAvailabilityWindowRequestDto request) {
        AvailabilityWindowResponseDto response = availabilityWindowService.createAvailabilityWindow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ALL_AVAILABILITY_WINDOW')")
    public ResponseEntity<List<AvailabilityWindowResponseDto>> getMyAvailabilityWindows() {

        List<AvailabilityWindowResponseDto> response = availabilityWindowService.getMyAvailabilityWindowsService();
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{availabilityId}/slots")
    @PreAuthorize("hasAuthority('VIEW_ALL_BOOKABLE_SLOTS')")
    public ResponseEntity<List<BookableSlotResponseDto>> getSlots(@PathVariable UUID availabilityId) {

        List<BookableSlotResponseDto> slots = bookableSlotService.getSlotsByAvailabilityWindow(availabilityId);

        return ResponseEntity.ok(slots);
    }


    @PutMapping("/{availabilityId}")
    @PreAuthorize("hasAuthority('UPDATE_AVAILABILITY_WINDOW')")
    public ResponseEntity<AvailabilityWindowResponseDto> updateAvailabilityWindow(@PathVariable UUID availabilityId, @Valid @RequestBody UpdateAvailabilityRequestDto request) {

        AvailabilityWindowResponseDto response = availabilityWindowService.updateAvailabilityWindow(availabilityId, request);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{availabilityId}")
    @PreAuthorize("hasAuthority('DELETE_AVAILABILITY_WINDOW')")
    public ResponseEntity<ApiResponse> deleteAvailabilityWindow(@PathVariable UUID availabilityId) {

        availabilityWindowService.deleteAvailabilityWindow(availabilityId);

        return ResponseEntity.ok(new ApiResponse(Instant.now(), 200, "Availability Window deleted successfully"));
    }


    @PreAuthorize("hasAuthority('CANCEL_SLOT')")
    @PostMapping("/slots/{slotId}/cancel")
    public ResponseEntity<ApiResponseDto<CancelSlotResponseDto>> cancelSlot(@PathVariable UUID slotId) {

        CancelSlotResponseDto result =
                bookableSlotService.cancelSlotByLecturer(slotId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Bookable slot cancelled successfully", result));
    }












}
