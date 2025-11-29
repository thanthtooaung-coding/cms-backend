package com.ecommerce.ecs.features.refund.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestRefundRequest {
    @NotNull
    private Long orderId;
    
    @NotBlank
    private String reason;
    
    private BigDecimal refundAmount; // Optional: if null, will use order total
}

