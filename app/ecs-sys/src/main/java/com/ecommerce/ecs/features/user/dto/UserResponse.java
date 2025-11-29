package com.ecommerce.ecs.features.user.dto;

import com.ecommerce.ecs.common.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String profileUrl;
    private String address;
    private String phoneNumber;
    private Long tenantId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

