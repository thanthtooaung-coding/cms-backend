package com.movie.celestix.common.enums;

import java.util.Arrays;

public enum Role {
    ADMIN(1L, "Admin"),
    CUSTOMER(3L, "Customer");

    private final Long id;
    private final String displayName;

    Role(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromId(Long id) {
        return Arrays.stream(Role.values())
                     .filter(r -> r.id.equals(id))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid Role ID: " + id));
    }
}
