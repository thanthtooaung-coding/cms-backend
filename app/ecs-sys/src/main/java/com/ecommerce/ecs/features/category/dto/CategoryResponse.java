package com.ecommerce.ecs.features.category.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long tenantId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

