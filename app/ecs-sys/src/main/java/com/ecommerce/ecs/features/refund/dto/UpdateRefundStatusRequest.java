package com.ecommerce.ecs.features.refund.dto;

import com.ecommerce.ecs.common.enums.RefundStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRefundStatusRequest {
    @NotNull
    private RefundStatus status;
}

