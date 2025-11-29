package com.ecommerce.ecs.features.promotion.dto;

import com.ecommerce.ecs.common.enums.PromotionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
public class PromotionResponse {
    private Long id;
    private String name;
    private String code;
    private PromotionType promotionType;
    private BigDecimal discountValue;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @JsonProperty("isActive")
    private boolean isActive;
    private Integer usageLimit;
    private Integer usedCount;
    private Long tenantId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

