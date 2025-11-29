package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum PaymentStatus {
    SUCCESS(1L, "Success"),
    FAIL(2L, "Fail");

    private final Long id;
    private final String displayName;

    PaymentStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() { return id; }

    public String getDisplayName() { return displayName; }

    public static PaymentStatus fromId(Long id) {
        return Arrays.stream(PaymentStatus.values())
                .filter(ps -> ps.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid PaymentStatus ID: " + id));
    }
}
