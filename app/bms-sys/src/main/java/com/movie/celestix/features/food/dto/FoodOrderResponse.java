package com.movie.celestix.features.food.dto;

import com.movie.celestix.common.enums.PaymentStatus;
import java.math.BigDecimal;

public record FoodOrderResponse(
    Long id,
    Long userId,
    PaymentStatus paymentStatus,
    BigDecimal totalPrice
) {}
