package com.ecommerce.ecs.features.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private long totalRevenue;
    private long pendingOrders;
    private long totalCategories;
    private long totalPromotions;
    private long totalReviews;
}

