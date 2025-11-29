package com.movie.celestix.features.foodcategories.dto;

public record UpdateFoodCategoryRequest(
        String name,
        String description
) {}