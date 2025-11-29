package com.movie.celestix.features.food.dto;

import java.util.List;

public record UpdateComboRequest(
        String comboName,
        List<Long> foodIds
) {}

