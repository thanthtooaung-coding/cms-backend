package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum BookingStatus {
    CONFIRMED(1L, "Confirmed"),
    CANCELLED(2L, "Cancelled");

    private Long id;
    private String displayName;

    BookingStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() { return id; }

    public String getDisplayName() { return displayName; }

    public static BookingStatus fromId(Long id) {
        return Arrays.stream(BookingStatus.values())
                .filter(bs -> bs.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid BookingStatus ID: " + id));
    }
}
