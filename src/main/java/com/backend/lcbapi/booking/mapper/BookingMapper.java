package com.backend.lcbapi.booking.mapper;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.booking.dto.response.CreateBookingResponseDto;
import com.backend.lcbapi.booking.dto.response.booking.*;
import com.backend.lcbapi.booking.entity.BookingEntity;
import org.springframework.stereotype.Component;



@Component
public class BookingMapper {



    public CreateBookingResponseDto toResponseDto(BookingEntity e){

        return CreateBookingResponseDto.builder()
                .id(e.getId())
                .slotId(e.getSlot().getId())
                .status(e.getStatus())
                .bookedAt(e.getBookedAt())
                .completedAt(e.getCompletedAt())
                .attendanceStatus(e.getAttendanceStatus())
                .build();
    }


    public StudentBookingResponseDto toStudentResponseView(BookingEntity booking) {

        BookableSlotEntity slot = booking.getSlot();
       AvailabilityWindowEntity availabilityWindow = slot.getAvailabilityWindow();
        LecturerEntity lecturerResponse = availabilityWindow.getLecturer();


        ConsultationDto consultation =
                new ConsultationDto(
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        availabilityWindow.getMode(),
                        availabilityWindow.getVenue(),
                        availabilityWindow.getMeetingLink(),
                        availabilityWindow.getCallInstruction()
                );

        AttendanceOutcomeDto attendanceOutcome =
                new AttendanceOutcomeDto(
                        booking.getAttendanceStatus(),
                        booking.getCompletedAt()
                );


        LecturerDto lecturer =
                new LecturerDto(
                        lecturerResponse.getId(),
                        lecturerResponse.getUser().getFirstName(),
                        lecturerResponse.getUser().getLastName(),
                        lecturerResponse.getDepartment()
                );

        CancelBookingResponseDto cancel =
                new CancelBookingResponseDto(
                        booking.getId(),
                        booking.getCancelledAt(),
                        booking.getStatus(),
                        booking.getSlot().getStatus(),
                        booking.getCancelledBy()
                );

        return new StudentBookingResponseDto(
                booking.getId(),
                booking.getStatus(),
                booking.getBookedAt(),
                lecturer,
                consultation,
                attendanceOutcome,
                cancel
        );
    }






    public LecturerBookingViewDto toLecturerBookingResponseView(BookingEntity booking) {

        BookableSlotEntity slot = booking.getSlot();
       AvailabilityWindowEntity availabilityWindow = slot.getAvailabilityWindow();
        StudentEntity studentResponse = booking.getStudent();


        ConsultationDto consultation =
                new ConsultationDto(
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        availabilityWindow.getMode(),
                        availabilityWindow.getVenue(),
                        availabilityWindow.getMeetingLink(),
                        availabilityWindow.getCallInstruction()
                );

        AttendanceOutcomeDto attendanceOutcome =
                new AttendanceOutcomeDto(
                        booking.getAttendanceStatus(),
                        booking.getCompletedAt()
                );


        StudentDto student =
                new StudentDto(
                        studentResponse.getStudentId(),
                        studentResponse.getUser().getFirstName(),
                        studentResponse.getUser().getLastName(),
                        studentResponse.getUser().getEmail()
                );

        CancelBookingResponseDto cancel =
                new CancelBookingResponseDto(
                        booking.getId(),
                        booking.getCancelledAt(),
                        booking.getStatus(),
                        booking.getSlot().getStatus(),
                        booking.getCancelledBy()

                );

        return new LecturerBookingViewDto(
                booking.getId(),
                booking.getStatus(),
                booking.getBookedAt(),
                student,
                consultation,
                attendanceOutcome,
                cancel
        );
    }



    public StudentBookingSummaryDto toStudentSummary(BookingEntity booking) {

        BookableSlotEntity slot =
                booking.getSlot();

        AvailabilityWindowEntity window =
                slot.getAvailabilityWindow();

        LecturerEntity lecturer =
                window.getLecturer();

        UserEntity lecturerUser =
                lecturer.getUser();

         LecturerDto lecturerDto =
                new LecturerDto(
                        lecturer.getId(),
                        lecturerUser.getFirstName(),
                        lecturerUser.getLastName(),
                        lecturer.getDepartment()
                );

        ConsultationDto consultationDto =
                 new ConsultationDto(
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        window.getMode(),
                        window.getVenue(),
                     window.getMeetingLink(),
                     window.getCallInstruction()

                );

        return
                new StudentBookingSummaryDto(
                booking.getId(),
                booking.getStatus(),
                booking.getBookedAt(),
                lecturerDto,
                consultationDto
        );
    }


    public LecturerBookingSummaryDto toLecturerSummary(BookingEntity booking) {

        BookableSlotEntity slot =
                booking.getSlot();

        AvailabilityWindowEntity window =
                slot.getAvailabilityWindow();

        StudentEntity student =
                booking.getStudent();

        UserEntity studentUser =
                student.getUser();

        StudentDto studentDto =
                new StudentDto(
                        studentUser.getStudent().getStudentId(),
                        studentUser.getFirstName(),
                        studentUser.getLastName(),
                        studentUser.getEmail()
                );

        ConsultationDto consultationDto =
                new ConsultationDto(
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        window.getMode(),
                        window.getVenue(),
                        window.getMeetingLink(),
                        window.getCallInstruction()
                );

        return
                new LecturerBookingSummaryDto(
                booking.getId(),
                booking.getStatus(),
                booking.getBookedAt(),
                studentDto,
                consultationDto
        );
    }



    public CancelBookingResponseDto toCancelBookingResponse(BookingEntity booking) {
            return
                    new CancelBookingResponseDto(
                        booking.getId(),
                        booking.getCancelledAt(),
                        booking.getStatus(),
                        booking.getSlot().getStatus(),
                        booking.getCancelledBy()

                    );


    }













}
