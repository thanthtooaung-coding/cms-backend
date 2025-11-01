package com.content_management_system.bms.features.dashboard.dto;

public record DashboardStatsResponse(
    long totalUsers,
    long totalCinemas,
    long totalMovies,
    long totalAdmins,
    long totalReservations
) {}
