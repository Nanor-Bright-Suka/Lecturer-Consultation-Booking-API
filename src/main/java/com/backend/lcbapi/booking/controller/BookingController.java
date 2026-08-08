package com.backend.lcbapi.booking.controller;


import com.backend.lcbapi.awmodule.dto.request.CreateAvailabilityWindowRequestDto;
import com.backend.lcbapi.awmodule.dto.response.AvailabilityWindowResponseDto;
import com.backend.lcbapi.booking.dto.response.ApiResponseDto;
import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;





    @PostMapping("/{slotId}")
    @PreAuthorize("hasAuthority('CREATE_BOOKING')")
    public ResponseEntity<ApiResponseDto<CreateBookingResponseDto>> create(@PathVariable UUID slotId) {
        System.out.println("The slot id " + slotId);
        CreateBookingResponseDto response = bookingService.createBookingService(slotId);
        return ResponseEntity.status(HttpStatus.CREATED).body( new ApiResponseDto<>("Booking created successfully", response));
    }











}
