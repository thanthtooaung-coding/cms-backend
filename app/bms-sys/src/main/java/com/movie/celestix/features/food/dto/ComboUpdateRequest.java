package com.movie.celestix.features.food.dto;

import java.util.List;

public class ComboUpdateRequest {
    private String comboName;
    private List<Long> foodIds;

    // Getters and setters
    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    public List<Long> getFoodIds() {
        return foodIds;
    }

    public void setFoodIds(List<Long> foodIds) {
        this.foodIds = foodIds;
    }
}
