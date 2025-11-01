package com.content_management_system.bms.features.food.controller;

import com.content_management_system.bms.common.models.Combo;
import com.content_management_system.bms.common.models.Food;
import com.content_management_system.bms.features.food.service.FoodService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/food")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<Food> all() {
        List<Food> foods = foodService.all();
        return foods;
    }

    @GetMapping("/{id}")
    public Optional<Food> getFoodById(@PathVariable Long id) {
        Optional<Food> foods = foodService.getFoodById(id);
        return foods;
    }

    @PostMapping
    public List<Food> add(@RequestBody Food food) {
        foodService.add(food);
        List<Food> foods = foodService.all();
        return foods;
    }

    @PatchMapping("/{id}")
    public List<Food> update(@PathVariable Long id, @RequestBody Food food) {
        foodService.modify(id, food);
        List<Food> foods = foodService.all();
        return foods;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Long id) {
        try {
            if (foodService.isIncludedInCombo(id)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("This item is included in a food combo. Please delete the combo first.");
            }
            foodService.drop(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }

    @GetMapping("/filter/{category}")
    public List<Food> filter(@PathVariable String category) {
        return foodService.filter(category);
    }

    // Get all combos
    @GetMapping("/combos")
    public List<Combo> getAllCombos() {
        return foodService.getAllCombos();
    }

    @GetMapping("/combos/{id}")
    public ResponseEntity<Combo> getComboById(@PathVariable Long id) {
        Optional<Combo> combo = foodService.getComboById(id);
        if (combo.isPresent()) {
            return ResponseEntity.ok(combo.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/combos")
    public ResponseEntity<?> createCombo(
            @RequestParam String comboName,
            @RequestBody List<Long> foodIds,
            @RequestParam String photoUrl) {
        try {
            Combo combo = foodService.createCombo(comboName, foodIds, photoUrl);
            return ResponseEntity.ok(combo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/combos/{id}")
    public ResponseEntity<Combo> updateCombo(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        String comboName = (String) updates.get("comboName");

        List<Long> foodIds = null;
        if (updates.get("foodIds") != null) {
            List<Integer> foodIdsInt = (List<Integer>) updates.get("foodIds");
            if (foodIdsInt != null) {
                foodIds = foodIdsInt.stream().map(Long::valueOf).toList();
            }
        }

        Combo updatedCombo = foodService.updateCombo(id, comboName, foodIds);
        return ResponseEntity.ok(updatedCombo);
    }

    // Delete a combo by ID
    @DeleteMapping("/combos/{id}")
    public String deleteCombo(@PathVariable Long id) {
        foodService.deleteCombo(id);
        return "Combo deleted successfully";
    }

}
