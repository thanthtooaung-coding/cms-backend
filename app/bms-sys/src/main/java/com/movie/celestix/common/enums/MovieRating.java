package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum MovieRating {
    G(1L, "1"),
    PG(2L, "2"),
    PG_13(3L, "3"),
    R(4L, "4"),
    NC_17(5L, "5");

    private final Long id;
    private final String displayName;

    MovieRating(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MovieRating fromId(Long id) {
        return Arrays.stream(MovieRating.values())
                .filter(r -> r.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid MovieRating ID: " + id));
    }
}
