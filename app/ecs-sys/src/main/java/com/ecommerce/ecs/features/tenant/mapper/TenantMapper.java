package com.ecommerce.ecs.features.tenant.mapper;

import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.features.tenant.dto.TenantRequest;
import com.ecommerce.ecs.features.tenant.dto.TenantResponse;

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

