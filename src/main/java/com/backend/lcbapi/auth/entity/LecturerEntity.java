package com.backend.lcbapi.auth.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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
