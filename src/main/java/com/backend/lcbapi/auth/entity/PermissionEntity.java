package com.backend.lcbapi.auth.entity;


import com.backend.lcbapi.auth.enums.PermissionEnum;
import com.backend.lcbapi.auth.enums.PermissionResourceEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Data
@Table(name = "permission")
public class PermissionEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PermissionEnum permissionName;


}