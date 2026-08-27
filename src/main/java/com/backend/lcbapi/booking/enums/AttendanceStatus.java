package com.backend.lcbapi.booking.enums;



public final class AttendanceStatus {

public enum AttendanceStatusEnum {
    BOTH_ATTENDED,
    BOTH_ABSENT,
    STUDENT_NO_SHOW,
    LECTURER_NO_SHOW,
    PENDING_APPROVAL
}



    public enum Student {
        ATTENDED,
        ABSENT
    }

    public enum Lecturer {
        BOTH_ATTENDED,
        STUDENT_NO_SHOW

    }


    private AttendanceStatus() {
    }

}