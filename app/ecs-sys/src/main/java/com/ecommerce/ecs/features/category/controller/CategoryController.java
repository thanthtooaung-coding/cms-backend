package com.ecommerce.ecs.features.category.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.category.dto.CategoryRequest;
import com.ecommerce.ecs.features.category.dto.CategoryResponse;
import com.ecommerce.ecs.features.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestBody CategoryRequest request,
            @RequestParam Long tenantId) {
        CategoryResponse response = categoryService.create(request, tenantId);
        return ApiResponse.created(response, "Category created successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(@RequestParam Long tenantId) {
        List<CategoryResponse> categories = categoryService.getAll(tenantId);
        return ApiResponse.ok(categories, "Categories retrieved successfully");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        CategoryResponse response = categoryService.getById(id, tenantId);
        return ApiResponse.ok(response, "Category retrieved successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @RequestBody CategoryRequest request,
            @RequestParam Long tenantId) {
        CategoryResponse response = categoryService.update(id, request, tenantId);
        return ApiResponse.ok(response, "Category updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        categoryService.delete(id, tenantId);
        return ApiResponse.noContent("Category deleted successfully");
    }
}

