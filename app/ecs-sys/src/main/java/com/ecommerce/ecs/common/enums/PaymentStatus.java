package com.ecommerce.ecs.common.enums;

import java.util.Arrays;

public enum PaymentStatus {
    PENDING(1L, "Pending"),
    PAID(2L, "Paid"),
    FAILED(3L, "Failed"),
    REFUNDED(4L, "Refunded");

    private final Long id;
    private final String displayName;

    PaymentStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentStatus fromId(Long id) {
        return Arrays.stream(PaymentStatus.values())
                     .filter(s -> s.id.equals(id))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid PaymentStatus ID: " + id));
    }
}

