package com.backend.lcbapi.awmodule.entity;


import com.backend.lcbapi.auth.entity.LecturerEntity;
import com.backend.lcbapi.awmodule.enums.AvailabilityModeEnum;
import com.backend.lcbapi.awmodule.enums.AvailabilityWindowStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table(name = "availability_window")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class AvailabilityWindowEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private LecturerEntity lecturer;

    @Column(name = "slot_duration", nullable = false)
    private Integer slotDuration;

    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityModeEnum mode;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AvailabilityWindowStatusEnum status = AvailabilityWindowStatusEnum.ACTIVE;

    private String meetingLink;

    private String callInstruction;

    private Instant createdAt;

    private Instant updatedAt;





}
