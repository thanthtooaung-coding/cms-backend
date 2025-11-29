package com.movie.celestix.features.food.mapper;

import com.movie.celestix.common.models.Food;
import com.movie.celestix.features.food.dto.FoodRequest;
import com.movie.celestix.features.food.dto.FoodResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FoodMapper {

    public FoodResponse toResponse(Food food) {
        if (food == null) {
            return null;
        }
        return new FoodResponse(
                food.getId(),
                food.getName(),
                food.getCategory(),
                food.getPrice(),
                food.getAllergens(),
                food.getDescription(),
                food.getPhotoUrl(),
                food.getCreatedAt(),
                food.getUpdatedAt()
        );
    }

    public List<FoodResponse> toResponseList(List<Food> foods) {
        if (foods == null) {
            return null;
        }
        return foods.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Food toEntity(FoodRequest request) {
        if (request == null) {
            return null;
        }
        Food food = new Food();
        food.setName(request.name());
        food.setCategory(request.category());
        food.setPrice(request.price() != null ? request.price() : 0.0);
        food.setAllergens(request.allergens());
        food.setDescription(request.description());
        food.setPhotoUrl(request.photoUrl());
        return food;
    }

    public void updateEntity(Food food, FoodRequest request) {
        if (food == null || request == null) {
            return;
        }
        if (request.name() != null) {
            food.setName(request.name());
        }
        if (request.category() != null) {
            food.setCategory(request.category());
        }
        if (request.price() != null) {
            food.setPrice(request.price());
        }
        if (request.allergens() != null) {
            food.setAllergens(request.allergens());
        }
        if (request.description() != null) {
            food.setDescription(request.description());
        }
        if (request.photoUrl() != null) {
            food.setPhotoUrl(request.photoUrl());
        }
    }
}

