package com.ecommerce.ecs.features.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private String imageUrl;
    @JsonProperty("isActive")
    private boolean isActive;
    private Long categoryId;
    private String categoryName;
    private Long tenantId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

