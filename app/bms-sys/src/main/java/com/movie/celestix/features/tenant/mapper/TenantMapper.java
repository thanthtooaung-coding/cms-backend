package com.movie.celestix.features.tenant.mapper;

import com.movie.celestix.features.tenant.dto.TenantRequest;
import com.movie.celestix.features.tenant.dto.TenantResponse;
import com.movie.celestix.common.models.Tenant;

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

