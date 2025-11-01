package com.content_management_system.bms.features.foodcategories.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.foodcategories.dto.FoodCategoryResponse;
import com.content_management_system.bms.features.foodcategories.dto.CreateFoodCategoryRequest;
import com.content_management_system.bms.features.foodcategories.dto.UpdateFoodCategoryRequest;
import com.content_management_system.bms.features.foodcategories.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/food-categories")
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<FoodCategoryResponse>> create(@RequestBody final CreateFoodCategoryRequest request) {
        final FoodCategoryResponse createdCategory = this.foodCategoryService.create(request);
        return ApiResponse.created(createdCategory, "Food category created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodCategoryResponse>> retrieveOne(@PathVariable final Long id) {
        return ApiResponse.ok(this.foodCategoryService.retrieveOne(id), "Food category retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodCategoryResponse>>> retrieveAll() {
        return ApiResponse.ok(this.foodCategoryService.retrieveAll(), "Food categories retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodCategoryResponse>> update(@PathVariable final Long id, @RequestBody final UpdateFoodCategoryRequest request) {
        return ApiResponse.ok(this.foodCategoryService.update(id, request), "Food category updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final Long id) {
        this.foodCategoryService.delete(id);
        return ApiResponse.noContent("Food category deleted successfully");
    }
}
