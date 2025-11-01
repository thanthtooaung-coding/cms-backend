package com.content_management_system.bms.features.theater.dto;

import java.math.BigDecimal;

public record SeatInfoData(
        int totalRows,
        BigDecimal totalPrice
) {}
