package com.code_room.auth_service.infrastructure.restclient.dto;

import com.code_room.auth_service.domain.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing a User.
 *
 * <p>This DTO is used for transferring user data between services and layers,
 * typically during REST client calls.</p>
 *
 * <p>Ignores unknown JSON properties during deserialization.</p>
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private String id;

    private String name;

    private String lastName;

    private String email;

    private String identification;

    private Role role;

    private String uriCvFile;
}
