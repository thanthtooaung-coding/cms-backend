package com.ecommerce.ecs.features.dashboard.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.dashboard.dto.DashboardStatsResponse;
import com.ecommerce.ecs.features.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats(@RequestParam Long tenantId) {
        DashboardStatsResponse stats = dashboardService.getDashboardStats(tenantId);
        return ApiResponse.ok(stats, "Dashboard statistics retrieved successfully");
    }
}

