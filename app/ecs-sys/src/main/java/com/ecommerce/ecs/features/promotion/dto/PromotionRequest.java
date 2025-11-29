package com.ecommerce.ecs.features.promotion.dto;

import com.ecommerce.ecs.common.enums.PromotionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String code;
    
    @NotNull
    private PromotionType promotionType;
    
    @NotNull
    private BigDecimal discountValue;
    
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive = true;
    private Integer usageLimit;
}

