package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum RefundStatus {
    PENDING(1L, "Pending"),
    APPROVED(2L, "Approved"),
    REJECTED(3L, "Rejected");

    private final Long id;
    private final String displayName;

    RefundStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RefundStatus fromId(Long id) {
        return Arrays.stream(RefundStatus.values())
                .filter(s -> s.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid RefundStatus ID: " + id));
    }
}
