package com.content_management_system.bms.features.foodcategories.dto;

public record UpdateFoodCategoryRequest(
        String name,
        String description
) {}