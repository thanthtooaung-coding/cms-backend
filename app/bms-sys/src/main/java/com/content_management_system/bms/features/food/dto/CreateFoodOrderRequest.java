package com.content_management_system.bms.features.food.dto;

import com.content_management_system.bms.features.booking.dto.CardDetails;
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