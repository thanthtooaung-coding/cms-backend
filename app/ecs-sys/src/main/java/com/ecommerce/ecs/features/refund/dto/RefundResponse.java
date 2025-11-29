package com.ecommerce.ecs.features.refund.dto;

import com.ecommerce.ecs.common.enums.RefundStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class RefundResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private RefundStatus status;
    private BigDecimal refundAmount;
    private String reason;
    private Long approvedBy;
    private String approvedByName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

