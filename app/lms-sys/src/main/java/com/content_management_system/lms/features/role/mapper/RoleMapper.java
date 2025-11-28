package com.content_management_system.lms.features.role.mapper;

import com.content_management_system.lms.features.role.dto.RoleResponse;
import com.content_management_system.lms.shared.entity.Role;

public class RoleMapper {
    public static RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}

