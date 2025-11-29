package com.ecommerce.ecs.common.enums;

import java.util.Arrays;

public enum OrderStatus {
    PENDING(1L, "Pending"),
    CONFIRMED(2L, "Confirmed"),
    PROCESSING(3L, "Processing"),
    SHIPPED(4L, "Shipped"),
    DELIVERED(5L, "Delivered"),
    CANCELLED(6L, "Cancelled");

    private final Long id;
    private final String displayName;

    OrderStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromId(Long id) {
        return Arrays.stream(OrderStatus.values())
                     .filter(s -> s.id.equals(id))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid OrderStatus ID: " + id));
    }
}

