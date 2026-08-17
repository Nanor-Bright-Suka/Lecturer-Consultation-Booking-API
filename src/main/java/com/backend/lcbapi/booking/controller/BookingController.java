package com.backend.lcbapi.booking.controller;


import com.backend.lcbapi.booking.dto.response.ApiResponseDto;
import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.dto.response.booking.*;
import com.backend.lcbapi.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;




    @PostMapping("/{slotId}")
    @PreAuthorize("hasAuthority('CREATE_BOOKING')")
    public ResponseEntity<ApiResponseDto<CreateBookingResponseDto>> create(@PathVariable UUID slotId) {
        CreateBookingResponseDto response = bookingService.createBookingService(slotId);
        return ResponseEntity.status(HttpStatus.CREATED).body( new ApiResponseDto<>("Booking created successfully", response));
    }



    @GetMapping("/student/{bookingId}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW_BOOKING')")
    public ResponseEntity<ApiResponseDto<StudentBookingResponseDto>> getStudentBooking(@PathVariable UUID bookingId) {

        StudentBookingResponseDto booking = bookingService.getStudentBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body( new ApiResponseDto<>("Booking retrieved successfully", booking));

    }

    @GetMapping("/lecturer/{bookingId}")
    @PreAuthorize("hasAuthority('LECTURER_VIEW_BOOKING')")
    public ResponseEntity<ApiResponseDto<LecturerBookingViewDto>> getLecturerBooking(@PathVariable UUID bookingId) {

        LecturerBookingViewDto booking = bookingService.getLecturerBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body( new ApiResponseDto<>("Booking retrieved successfully", booking));

    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('STUDENT_VIEW_ALL_BOOKINGS')")
    public ResponseEntity<ApiResponseDto<List<StudentBookingSummaryDto>>> getAllStudentBooking() {

        List<StudentBookingSummaryDto> booking =
                bookingService.getAllStudentBookings();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Booking retrieved successfully", booking));

    }

    @GetMapping("/lecturer")
    @PreAuthorize("hasAuthority('LECTURER_VIEW_ALL_BOOKINGS')")
    public ResponseEntity<ApiResponseDto<List<LecturerBookingSummaryDto>>> getAllLecturerBooking() {

        List<LecturerBookingSummaryDto> booking =
                                       bookingService.getAllLecturerBookings();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Booking retrieved successfully", booking));

    }



    @PreAuthorize("hasAuthority('STUDENT_CANCEL_BOOKING')")
    @PostMapping("/student/{bookingId}/cancel")
    public ResponseEntity<ApiResponseDto<CancelBookingResponseDto>> cancelStudentBooking(@PathVariable UUID bookingId) {

        CancelBookingResponseDto result =
                               bookingService.cancelBookingByStudent(bookingId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Booking cancelled successfully", result));
    }



    @PreAuthorize("hasAuthority('LECTURER_CANCEL_BOOKING')")
    @PostMapping("/lecturer/{bookingId}/cancel")
    public ResponseEntity<ApiResponseDto<CancelBookingResponseDto>> cancelLecturerBooking(@PathVariable UUID bookingId) {

        CancelBookingResponseDto result =
                               bookingService.cancelBookingByLecturer(bookingId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Booking cancelled successfully", result));
    }













}
