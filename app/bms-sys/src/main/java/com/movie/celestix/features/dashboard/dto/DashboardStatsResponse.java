package com.movie.celestix.features.dashboard.dto;

public record DashboardStatsResponse(
    long totalUsers,
    long totalCinemas,
    long totalMovies,
    long totalAdmins,
    long totalReservations
) {}
