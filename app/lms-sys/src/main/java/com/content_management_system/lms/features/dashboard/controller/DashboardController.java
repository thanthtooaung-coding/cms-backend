package com.content_management_system.lms.features.dashboard.controller;

import com.content_management_system.lms.features.dashboard.dto.DashboardStatsResponse;
import com.content_management_system.lms.features.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats(
            @RequestParam Long tenantId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        DashboardStatsResponse stats = dashboardService.getDashboardStats(tenantId, userId);
        return ResponseEntity.ok(stats);
    }
}

