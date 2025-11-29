package com.movie.celestix.features.food.dto;

import com.movie.celestix.features.booking.dto.CardDetails;
import java.util.List;

public record CreateFoodOrderRequest(
        List<OrderItemData> items,
        CardDetails cardDetails
) {
    public record OrderItemData(
            String id,
            int quantity,
            String type
    ) {}
}