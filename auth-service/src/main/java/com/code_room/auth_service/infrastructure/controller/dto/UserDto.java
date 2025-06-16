package com.code_room.auth_service.infrastructure.controller.dto;
import com.code_room.auth_service.domain.Enum.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String name;
    private String lastName;
    private String email;
    private String identification;
    private Role role;
}
