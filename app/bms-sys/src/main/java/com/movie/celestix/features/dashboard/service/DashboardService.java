package com.movie.celestix.features.dashboard.service;

import com.movie.celestix.features.dashboard.dto.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats(Long tenantId);
}
