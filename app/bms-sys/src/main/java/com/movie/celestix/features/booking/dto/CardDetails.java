package com.movie.celestix.features.booking.dto;

public record CardDetails(
        String cardNumber,
        String expiryDate,
        String cvc
) {}
