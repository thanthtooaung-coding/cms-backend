package com.movie.celestix.features.foodcategories.service;

import com.movie.celestix.features.foodcategories.dto.FoodCategoryResponse;
import com.movie.celestix.features.foodcategories.dto.CreateFoodCategoryRequest;
import com.movie.celestix.features.foodcategories.dto.UpdateFoodCategoryRequest;

import java.util.List;

public interface FoodCategoryService {
    FoodCategoryResponse create(CreateFoodCategoryRequest request, Long tenantId);
    FoodCategoryResponse retrieveOne(Long id, Long tenantId);
    List<FoodCategoryResponse> retrieveAll(Long tenantId);
    FoodCategoryResponse update(Long id, UpdateFoodCategoryRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}
