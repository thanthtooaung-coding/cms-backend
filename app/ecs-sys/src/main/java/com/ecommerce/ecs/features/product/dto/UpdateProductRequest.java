package com.ecommerce.ecs.features.product.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private String imageUrl;
    private Long categoryId;
    private Boolean isActive;
}

