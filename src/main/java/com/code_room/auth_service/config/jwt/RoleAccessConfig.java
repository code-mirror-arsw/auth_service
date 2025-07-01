package com.code_room.auth_service.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "role-access")
@Getter
@Setter
public class RoleAccessConfig {

    private Map<String, List<String>> access = new HashMap<>();
}
