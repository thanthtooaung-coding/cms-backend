package com.content_management_system.bms.features.food.dto;

import java.math.BigDecimal;

public record FoodOrderItemDetails(
        String itemName,
        int quantity,
        BigDecimal price
) {}
