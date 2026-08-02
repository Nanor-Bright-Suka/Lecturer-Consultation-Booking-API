package com.backend.lcbapi.auth.entity;


import com.backend.lcbapi.awmodule.entity.AvailabilityWindowEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Table(name = "lecturer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class LecturerEntity {


    @Id
    private UUID id;

    @Column(name = "lecturer", unique = true, nullable = false)
    private String staffId;

    private String department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;



}
