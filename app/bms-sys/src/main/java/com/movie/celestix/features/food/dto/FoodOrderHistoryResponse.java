package com.movie.celestix.features.food.dto;

import com.movie.celestix.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record FoodOrderHistoryResponse(
        Long id,
        OffsetDateTime orderDate,
        PaymentStatus paymentStatus,
        BigDecimal totalPrice,
        List<FoodOrderItemDetails> items
) {}
