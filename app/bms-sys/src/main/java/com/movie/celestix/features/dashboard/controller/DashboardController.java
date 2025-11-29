package com.movie.celestix.features.dashboard.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.dashboard.dto.DashboardStatsResponse;
import com.movie.celestix.features.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats(@RequestParam(required = false) Long tenantId) {
        DashboardStatsResponse stats = dashboardService.getDashboardStats(tenantId);
        return ApiResponse.ok(stats, "Dashboard statistics retrieved successfully");
    }
}
