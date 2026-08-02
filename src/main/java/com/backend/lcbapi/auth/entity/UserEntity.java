package com.backend.lcbapi.auth.entity;


import com.backend.lcbapi.auth.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Table(name = "my_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class UserEntity {


     @Id
     private UUID userId;

     private String firstName;

     private String lastName;

     private String email;

     private String password;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<RefreshTokenEntity> refreshTokens = new HashSet<>();



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private StudentEntity student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private LecturerEntity lecturer;

    public void addRole(RoleEntity role) {
        this.roles.add(role);
    }

    public boolean hasRole(RoleEnum roleName) {
        return roles.stream().anyMatch(role -> role.getRoleName() == roleName);
    }


}
