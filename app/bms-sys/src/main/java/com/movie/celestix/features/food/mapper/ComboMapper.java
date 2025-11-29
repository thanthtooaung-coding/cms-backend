package com.movie.celestix.features.food.mapper;

import com.movie.celestix.common.models.Combo;
import com.movie.celestix.features.food.dto.ComboResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComboMapper {

    private final FoodMapper foodMapper;

    public ComboMapper(FoodMapper foodMapper) {
        this.foodMapper = foodMapper;
    }

    public ComboResponse toResponse(Combo combo) {
        if (combo == null) {
            return null;
        }
        return new ComboResponse(
                combo.getId(),
                combo.getComboName(),
                combo.getComboPrice(),
                combo.getPhotoUrl(),
                combo.getFoods() != null ? foodMapper.toResponseList(combo.getFoods()) : null,
                combo.getCreatedAt(),
                combo.getUpdatedAt()
        );
    }

    public List<ComboResponse> toResponseList(List<Combo> combos) {
        if (combos == null) {
            return null;
        }
        return combos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

