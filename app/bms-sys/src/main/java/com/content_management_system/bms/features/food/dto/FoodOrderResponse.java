package com.content_management_system.bms.features.food.dto;

import com.content_management_system.bms.common.enums.PaymentStatus;
import java.math.BigDecimal;

public record FoodOrderResponse(
    Long id,
    Long userId,
    PaymentStatus paymentStatus,
    BigDecimal totalPrice
) {}
