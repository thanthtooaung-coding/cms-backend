package com.ecommerce.ecs.features.dashboard.service;

import com.ecommerce.ecs.features.dashboard.dto.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats(Long tenantId);
}

