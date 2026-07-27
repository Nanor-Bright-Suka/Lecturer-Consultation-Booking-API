package com.backend.lcbapi.auth.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class StudentEntity {

    @Id
    private UUID id;

    @Column(name = "index_number", unique = true, nullable = false)
    private String studentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

}
