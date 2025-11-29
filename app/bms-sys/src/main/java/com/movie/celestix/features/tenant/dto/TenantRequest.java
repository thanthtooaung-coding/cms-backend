package com.movie.celestix.features.tenant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantRequest {
    private String name;
    private boolean isActive;
}

