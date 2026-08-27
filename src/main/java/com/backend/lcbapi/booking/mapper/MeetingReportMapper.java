package com.backend.lcbapi.booking.mapper;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.auth.entity.StudentEntity;
import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.booking.dto.response.consultation.*;
import com.backend.lcbapi.booking.entity.BookingEntity;
import com.backend.lcbapi.booking.entity.MeetingReportEntity;
import org.springframework.stereotype.Component;

@Component
public class MeetingReportMapper {


    public MeetingReportDetailResponse toDetailResponse(MeetingReportEntity report) {

        BookingEntity booking = report.getBooking();

        BookableSlotEntity slot = booking.getSlot();

        AvailabilityWindowEntity window =
                                          slot.getAvailabilityWindow();

        StudentEntity student =
                                booking.getStudent();

        UserEntity studentUser =
                                 student.getUser();

        LecturerEntity lecturer =
                                   window.getLecturer();

        UserEntity lecturerUser =
                                    lecturer.getUser();


        return new MeetingReportDetailResponse(

                report.getId(),

                report.getStatus(),

                report.getDescription(),

                report.getCreatedAt(),

                report.getUpdatedAt(),

                new StudentReportInfo(
                        student.getId(),
                        student.getStudentId(),
                        studentUser.getFirstName()
                                + " "
                                + studentUser.getLastName(),
                        studentUser.getEmail()
                ),

                new LecturerReportInfo(
                        lecturer.getId(),
                        lecturerUser.getFirstName()
                                + " "
                                + lecturerUser.getLastName(),
                        lecturerUser.getEmail(),
                        lecturer.getStaffId(),
                        lecturer.getDepartment()
                ),

                new BookingReportInfo(
                        booking.getId(),
                        booking.getStatus(),
                        booking.getAttendanceStatus(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        window.getMode(),
                        window.getVenue(),
                        window.getMeetingLink()
                ),

                new ReviewReportInfo(
                        report.getReviewedAt(),
                        report.getReason()
                )
        );
    }









}
