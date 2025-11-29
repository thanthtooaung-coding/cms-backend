package com.movie.celestix.features.food.dto;

import java.math.BigDecimal;

public record FoodOrderItemDetails(
        String itemName,
        int quantity,
        BigDecimal price
) {}
