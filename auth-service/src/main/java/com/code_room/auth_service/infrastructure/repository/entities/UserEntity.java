package com.code_room.auth_service.infrastructure.repository.entities;

import com.code_room.auth_service.domain.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @Column(columnDefinition = "VARCHAR(36)", nullable = false)
    private String id;

    private String name;
    private String lastName;
    private String email;
    private String password;
    private String identification;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "enabled")
    private boolean enabled = false;


    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}


