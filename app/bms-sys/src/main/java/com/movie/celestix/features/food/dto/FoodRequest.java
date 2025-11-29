package com.movie.celestix.features.food.dto;

import com.movie.celestix.common.enums.Category;

public record FoodRequest(
        String name,
        Category category,
        Double price,
        String allergens,
        String description,
        String photoUrl
) {}

