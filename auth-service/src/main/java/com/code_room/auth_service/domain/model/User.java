package com.code_room.auth_service.domain.model;

import com.code_room.auth_service.domain.Enum.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String identification;

    @Enumerated(EnumType.STRING)
    private Role role;
}
