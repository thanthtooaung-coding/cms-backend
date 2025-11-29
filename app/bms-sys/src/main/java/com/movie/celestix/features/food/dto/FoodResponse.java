package com.movie.celestix.features.food.dto;

import com.movie.celestix.common.enums.Category;
import java.time.OffsetDateTime;

public record FoodResponse(
        Long id,
        String name,
        Category category,
        Double price,
        String allergens,
        String description,
        String photoUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

