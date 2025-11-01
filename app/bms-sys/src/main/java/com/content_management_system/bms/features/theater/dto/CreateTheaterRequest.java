package com.content_management_system.bms.features.theater.dto;

public record CreateTheaterRequest(
        String name,
        String location,
        SeatConfigurationData seatConfiguration,
        SeatInfoData premiumSeat,
        SeatInfoData regularSeat,
        SeatInfoData economySeat,
        SeatInfoData basicSeat
) {}
