package com.content_management_system.lms.features.tenant.mapper;

import com.content_management_system.lms.features.tenant.dto.TenantRequest;
import com.content_management_system.lms.features.tenant.dto.TenantResponse;
import com.content_management_system.lms.shared.entity.Tenant;

public class TenantMapper {

    public static Tenant toEntity(TenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setActive(request.isActive());
        return tenant;
    }

    public static TenantResponse toResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .isActive(tenant.isActive())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}