package com.movie.celestix.features.foodcategories.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.foodcategories.dto.FoodCategoryResponse;
import com.movie.celestix.features.foodcategories.dto.CreateFoodCategoryRequest;
import com.movie.celestix.features.foodcategories.dto.UpdateFoodCategoryRequest;
import com.movie.celestix.features.foodcategories.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food-categories")
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<FoodCategoryResponse>> create(
            @RequestBody final CreateFoodCategoryRequest request,
            @RequestParam Long tenantId
    ) {
        final FoodCategoryResponse createdCategory = this.foodCategoryService.create(request, tenantId);
        return ApiResponse.created(createdCategory, "Food category created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodCategoryResponse>> retrieveOne(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(this.foodCategoryService.retrieveOne(id, tenantId), "Food category retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodCategoryResponse>>> retrieveAll(@RequestParam Long tenantId) {
        return ApiResponse.ok(this.foodCategoryService.retrieveAll(tenantId), "Food categories retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodCategoryResponse>> update(
            @PathVariable final Long id,
            @RequestBody final UpdateFoodCategoryRequest request,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(this.foodCategoryService.update(id, request, tenantId), "Food category updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        this.foodCategoryService.delete(id, tenantId);
        return ApiResponse.noContent("Food category deleted successfully");
    }
}
