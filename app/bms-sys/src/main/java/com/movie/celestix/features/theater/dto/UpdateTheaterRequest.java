package com.movie.celestix.features.theater.dto;

public record UpdateTheaterRequest(
        String name,
        String location,
        SeatConfigurationData seatConfiguration,
        SeatInfoData premiumSeat,
        SeatInfoData regularSeat,
        SeatInfoData economySeat,
        SeatInfoData basicSeat
) {}
