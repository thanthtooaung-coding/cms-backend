package com.movie.celestix.features.food.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.food.dto.*;
import com.movie.celestix.features.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodResponse>>> all(@RequestParam Long tenantId) {
        List<FoodResponse> foods = foodService.all(tenantId);
        if (foods == null || foods.isEmpty()) {
            return ApiResponse.ok(foods, "No food items found. Please add food items to the menu.");
        }
        return ApiResponse.ok(foods, "Foods retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> getFoodById(@PathVariable Long id, @RequestParam Long tenantId) {
        Optional<FoodResponse> food = foodService.getFoodById(id, tenantId);
        if (food.isPresent()) {
            return ApiResponse.ok(food.get(), "Food retrieved successfully");
        } else {
            return ApiResponse.badRequest("Food item not found with the provided ID. Please check the ID and try again.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodResponse>> add(@RequestBody FoodRequest request, @RequestParam Long tenantId) {
        FoodResponse food = foodService.add(request, tenantId);
        return ApiResponse.created(food, "Food created successfully");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> update(@PathVariable Long id, @RequestBody FoodRequest request, @RequestParam Long tenantId) {
        FoodResponse food = foodService.modify(id, request, tenantId);
        return ApiResponse.ok(food, "Food updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFood(@PathVariable Long id, @RequestParam Long tenantId) {
        try {
            if (foodService.isIncludedInCombo(id)) {
                return ApiResponse.conflict("This item is included in a food combo. Please delete the combo first.");
            }
            foodService.drop(id, tenantId);
            return ApiResponse.noContent("Food deleted successfully");
        } catch (Exception e) {
            return ApiResponse.badRequest("Unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/filter/{category}")
    public ResponseEntity<ApiResponse<List<FoodResponse>>> filter(@PathVariable String category, @RequestParam Long tenantId) {
        List<FoodResponse> foods = foodService.filter(category, tenantId);
        if (foods == null || foods.isEmpty()) {
            String categoryName = category.equalsIgnoreCase("none") ? "all categories" : category.toLowerCase();
            return ApiResponse.ok(foods, "No food items found in the " + categoryName + " category. Please add food items to this category.");
        }
        return ApiResponse.ok(foods, "Foods filtered successfully");
    }

    // Get all combos
    @GetMapping("/combos")
    public ResponseEntity<ApiResponse<List<ComboResponse>>> getAllCombos(@RequestParam Long tenantId) {
        List<ComboResponse> combos = foodService.getAllCombos(tenantId);
        if (combos == null || combos.isEmpty()) {
            return ApiResponse.ok(combos, "No food combos found. Please create food combos to offer special deals to customers.");
        }
        return ApiResponse.ok(combos, "Combos retrieved successfully");
    }

    @GetMapping("/combos/{id}")
    public ResponseEntity<ApiResponse<ComboResponse>> getComboById(@PathVariable Long id, @RequestParam Long tenantId) {
        Optional<ComboResponse> combo = foodService.getComboById(id, tenantId);
        if (combo.isPresent()) {
            return ApiResponse.ok(combo.get(), "Combo retrieved successfully");
        } else {
            return ApiResponse.badRequest("Food combo not found with the provided ID. Please check the ID and try again.");
        }
    }

    @PostMapping("/combos")
    public ResponseEntity<ApiResponse<ComboResponse>> createCombo(
            @RequestBody CreateComboRequest request,
            @RequestParam Long tenantId) {
        try {
            ComboResponse combo = foodService.createCombo(request, tenantId);
            return ApiResponse.created(combo, "Combo created successfully");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @PatchMapping("/combos/{id}")
    public ResponseEntity<ApiResponse<ComboResponse>> updateCombo(
            @PathVariable Long id,
            @RequestBody UpdateComboRequest request,
            @RequestParam Long tenantId) {
        try {
            ComboResponse updatedCombo = foodService.updateCombo(id, request, tenantId);
            return ApiResponse.ok(updatedCombo, "Combo updated successfully");
        } catch (Exception e) {
            return ApiResponse.badRequest("Failed to update combo: " + e.getMessage());
        }
    }

    // Delete a combo by ID
    @DeleteMapping("/combos/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCombo(@PathVariable Long id, @RequestParam Long tenantId) {
        try {
            foodService.deleteCombo(id, tenantId);
            return ApiResponse.noContent("Combo deleted successfully");
        } catch (Exception e) {
            return ApiResponse.badRequest("Failed to delete combo: " + e.getMessage());
        }
    }

}
