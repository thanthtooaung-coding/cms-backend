package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum MovieLanguage {
    ENGLISH(1L, "English"),
    SPANISH(2L, "Spanish"),
    FRENCH(3L, "French"),
    GERMAN(4L, "German"),
    HINDI(5L, "Hindi");

    private final Long id;
    private final String displayName;

    MovieLanguage(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MovieLanguage fromId(Long id) {
        return Arrays.stream(MovieLanguage.values())
                .filter(l -> l.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid MovieLanguage ID: " + id));
    }
}
