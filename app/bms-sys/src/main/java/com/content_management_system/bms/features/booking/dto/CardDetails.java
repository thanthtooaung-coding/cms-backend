package com.content_management_system.bms.features.booking.dto;

public record CardDetails(
        String cardNumber,
        String expiryDate,
        String cvc
) {}
