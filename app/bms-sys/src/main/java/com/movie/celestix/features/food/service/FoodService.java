package com.movie.celestix.features.food.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.movie.celestix.common.exception.ResourceNotFoundException;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jpa.ComboJpaRepository;
import com.movie.celestix.common.repository.jpa.FoodJpaRepository;
import com.movie.celestix.features.food.dto.*;
import com.movie.celestix.features.food.mapper.ComboMapper;
import com.movie.celestix.features.food.mapper.FoodMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.movie.celestix.common.enums.Category;
import com.movie.celestix.common.models.Combo;
import com.movie.celestix.common.models.Food;

@Service
@AllArgsConstructor
public class FoodService {
    private final FoodJpaRepository foodRepo;
    private final ComboJpaRepository comboRepo;
    private final TenantRepository tenantRepository;
    private final FoodMapper foodMapper;
    private final ComboMapper comboMapper;

    public List<FoodResponse> all(Long tenantId) {
        List<Food> foods = foodRepo.findAllByTenantId(tenantId);
        return foodMapper.toResponseList(foods);
    }

    public Optional<FoodResponse> getFoodById(Long id, Long tenantId) {
        Optional<Food> food = foodRepo.findById(id);
        if (food.isPresent() && (food.get().getTenant() == null || !food.get().getTenant().getId().equals(tenantId))) {
            return Optional.empty();
        }
        return food.map(foodMapper::toResponse);
    }

    public FoodResponse add(FoodRequest request, Long tenantId) {
        Food food = foodMapper.toEntity(request);
        
        // Set tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        food.setTenant(tenant);
        
        Food savedFood = foodRepo.save(food);
        return foodMapper.toResponse(savedFood);
    }

    public FoodResponse modify(Long id, FoodRequest request, Long tenantId) {
        Food food = foodRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food with id " + id + " not found"));
        
        // Verify tenant matches
        if (food.getTenant() == null || !food.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Food with id " + id + " not found for tenant " + tenantId);
        }
        
        foodMapper.updateEntity(food, request);
        Food savedFood = foodRepo.save(food);

        // Recalculate combo prices if price changed
        if (request.price() != null && request.price() != 0.0) {
            List<Combo> combosToUpdate = comboRepo.findByFoods_Id(food.getId());
            for (Combo combo : combosToUpdate) {
                if (combo.getTenant() != null && combo.getTenant().getId().equals(tenantId)) {
                    recalculateComboPrice(combo);
                    comboRepo.save(combo);
                }
            }
        }
        
        return foodMapper.toResponse(savedFood);
    }

    public void drop(Long id, Long tenantId) {
        Food food = foodRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Food with id " + id + " not found"));
        // Verify tenant matches
        if (food.getTenant() == null || !food.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Food with id " + id + " not found for tenant " + tenantId);
        }
        foodRepo.deleteById(id);
    }

    public boolean isIncludedInCombo(Long foodId) {
        return comboRepo.existsByFoods_Id(foodId);
    }

    public List<FoodResponse> filter(String category, Long tenantId) {
        List<Food> foods;
        if (category.equalsIgnoreCase("none")) {
            foods = foodRepo.findAllByTenantId(tenantId);
        } else {
            try {
                Category catEnum = Category.valueOf(category.toUpperCase());
                foods = foodRepo.findByCategoryAndTenantId(catEnum, tenantId);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category! Only snacks, drinks, food, or none are allowed.");
            }
        }
        return foodMapper.toResponseList(foods);
    }

    public List<ComboResponse> getAllCombos(Long tenantId) {
        List<Combo> combos = comboRepo.findAllByTenantId(tenantId);
        return comboMapper.toResponseList(combos);
    }

    public Optional<ComboResponse> getComboById(Long id, Long tenantId) {
        Optional<Combo> combo = comboRepo.findById(id);
        if (combo.isPresent() && (combo.get().getTenant() == null || !combo.get().getTenant().getId().equals(tenantId))) {
            return Optional.empty();
        }
        return combo.map(comboMapper::toResponse);
    }

    public ComboResponse createCombo(CreateComboRequest request, Long tenantId) {
        List<Long> foodIds = request.foodIds();
        
        // ✅ Validation rules
        if (foodIds == null || foodIds.size() < 2) {
            throw new IllegalArgumentException("A combo must have at least 2 food items.");
        }
        if (foodIds.size() > 5) {
            throw new IllegalArgumentException("A combo cannot exceed 5 food items.");
        }

        // Fetch selected foods (preserve duplicates)
        List<Food> selectedFoods = foodIds.stream()
                .map(id -> foodRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Food not found: " + id)))
                .toList();

        // Calculate total price
        double sumPrice = selectedFoods.stream()
                .mapToDouble(Food::getPrice)
                .sum();

        // Apply discount
        double discountRate = switch (foodIds.size()) {
            case 2 -> 0.03;
            case 3 -> 0.05;
            case 4 -> 0.08;
            case 5 -> 0.10;
            default -> 0.0;
        };

        double finalPrice = sumPrice * (1 - discountRate);

        // Create combo
        Combo combo = new Combo();
        combo.setComboName(request.comboName());
        combo.setFoods(selectedFoods); // ✅ keeps duplicates
        combo.setComboPrice(finalPrice);
        combo.setPhotoUrl(request.photoUrl());
        
        // Set tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        combo.setTenant(tenant);
        
        Combo savedCombo = comboRepo.save(combo);
        return comboMapper.toResponse(savedCombo);
    }

    public ComboResponse updateCombo(Long comboId, UpdateComboRequest request, Long tenantId) {
        Combo combo = comboRepo.findById(comboId)
                .orElseThrow(() -> new RuntimeException("Combo not found"));
        // Verify tenant matches
        if (combo.getTenant() == null || !combo.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Combo with id " + comboId + " not found for tenant " + tenantId);
        }

        if (request.comboName() != null) {
            combo.setComboName(request.comboName());
        }

        if (request.foodIds() != null && !request.foodIds().isEmpty()) {
            // ✅ Preserve duplicates by fetching manually
            List<Food> selectedFoods = new ArrayList<>();
            for (Long id : request.foodIds()) {
                foodRepo.findById(id).ifPresent(selectedFoods::add);
                // if not found → skip (no crash)
            }

            combo.setFoods(selectedFoods);

            // ✅ Sum price including duplicates
            double sumPrice = selectedFoods.stream()
                    .mapToDouble(Food::getPrice)
                    .sum();

            // ✅ Apply discount including duplicates
            double discountRate = switch (selectedFoods.size()) {
                case 2 -> 0.03;
                case 3 -> 0.05;
                case 4 -> 0.08;
                case 5 -> 0.10;
                default -> 0.0;
            };

            double finalPrice = sumPrice * (1 - discountRate);
            combo.setComboPrice(finalPrice);
        }

        Combo savedCombo = comboRepo.save(combo);
        return comboMapper.toResponse(savedCombo);
    }

    @Transactional
    public void deleteCombo(Long id, Long tenantId) {
        Combo combo = comboRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Combo with id " + id + " not found"));
        // Verify tenant matches
        if (combo.getTenant() == null || !combo.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Combo with id " + id + " not found for tenant " + tenantId);
        }
        comboRepo.deleteById(id);
    }

    private void recalculateComboPrice(Combo combo) {
        List<Food> selectedFoods = combo.getFoods();
        double sumPrice = selectedFoods.stream()
                .mapToDouble(Food::getPrice)
                .sum();

        double discountRate = switch (selectedFoods.size()) {
            case 2 -> 0.03;
            case 3 -> 0.05;
            case 4 -> 0.08;
            case 5 -> 0.10;
            default -> 0.0;
        };

        double finalPrice = sumPrice * (1 - discountRate);
        combo.setComboPrice(finalPrice);
    }
}
