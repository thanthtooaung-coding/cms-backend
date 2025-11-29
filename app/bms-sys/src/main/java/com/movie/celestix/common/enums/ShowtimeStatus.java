package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum ShowtimeStatus {
    AVAILABLE(1L, "Available");

    private final Long id;
    private final String displayName;

    ShowtimeStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ShowtimeStatus fromId(Long id) {
        return Arrays.stream(ShowtimeStatus.values())
                .filter(s -> s.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid ShowtimeStatus ID: " + id));
    }
}
