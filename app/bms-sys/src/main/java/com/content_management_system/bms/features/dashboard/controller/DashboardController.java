package com.content_management_system.bms.features.dashboard.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.dashboard.dto.DashboardStatsResponse;
import com.content_management_system.bms.features.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ApiResponse.ok(stats, "Dashboard statistics retrieved successfully");
    }
}
