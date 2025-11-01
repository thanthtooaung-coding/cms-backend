package com.content_management_system.bms.features.foodcategories.service;

import com.content_management_system.bms.features.foodcategories.dto.FoodCategoryResponse;
import com.content_management_system.bms.features.foodcategories.dto.CreateFoodCategoryRequest;
import com.content_management_system.bms.features.foodcategories.dto.UpdateFoodCategoryRequest;

import java.util.List;

public interface FoodCategoryService {
    FoodCategoryResponse create(CreateFoodCategoryRequest request);
    FoodCategoryResponse retrieveOne(Long id);
    List<FoodCategoryResponse> retrieveAll();
    FoodCategoryResponse update(Long id, UpdateFoodCategoryRequest request);
    void delete(Long id);
}
