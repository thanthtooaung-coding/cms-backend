package com.content_management_system.lms.features.dashboard.service;

import com.content_management_system.lms.features.dashboard.dto.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats(Long tenantId, Long userId);
}

