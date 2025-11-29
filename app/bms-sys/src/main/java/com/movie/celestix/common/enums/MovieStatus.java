package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum MovieStatus {
    NOW_SHOWING(1L, "Now Showing"),
    COMING_SOON(2L, "Coming Soon");

    private final Long id;
    private final String displayName;

    MovieStatus(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MovieStatus fromId(Long id) {
        return Arrays.stream(MovieStatus.values())
                .filter(s -> s.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid MovieStatus ID: " + id));
    }
}
