package com.movie.celestix.features.food.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ComboResponse(
        Long id,
        String comboName,
        Double comboPrice,
        String photoUrl,
        List<FoodResponse> foods,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

