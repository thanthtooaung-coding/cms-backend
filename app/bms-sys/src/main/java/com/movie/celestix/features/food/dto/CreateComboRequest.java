package com.movie.celestix.features.food.dto;

import java.util.List;

public record CreateComboRequest(
        String comboName,
        List<Long> foodIds,
        String photoUrl
) {}

