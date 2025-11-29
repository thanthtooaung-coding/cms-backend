package com.movie.celestix.features.theater.dto;

import java.math.BigDecimal;

public record SeatInfoData(
        int totalRows,
        BigDecimal totalPrice
) {}
