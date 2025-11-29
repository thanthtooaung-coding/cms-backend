package com.ecommerce.ecs.features.dashboard.service.impl;

import com.ecommerce.ecs.common.enums.OrderStatus;
import com.ecommerce.ecs.common.enums.PaymentStatus;
import com.ecommerce.ecs.common.enums.RefundStatus;
import com.ecommerce.ecs.common.enums.Role;
import com.ecommerce.ecs.common.repository.jpa.*;
import com.ecommerce.ecs.features.dashboard.dto.DashboardStatsResponse;
import com.ecommerce.ecs.features.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final UserJpaRepository userJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final PromotionJpaRepository promotionJpaRepository;
    private final ReviewJpaRepository reviewJpaRepository;
    private final RefundJpaRepository refundJpaRepository;
    
    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Long tenantId) {
        long totalUsers = userJpaRepository.countByTenantId(tenantId);
        long totalProducts = productJpaRepository.countByTenantId(tenantId);
        long totalOrders = orderJpaRepository.countByTenantId(tenantId);
        long totalCategories = categoryJpaRepository.countByTenantId(tenantId);
        long totalPromotions = promotionJpaRepository.findAllByTenantId(tenantId).size();
        long totalReviews = reviewJpaRepository.findAllByProductTenantId(tenantId).size();
        
        // Calculate total revenue from paid orders
        BigDecimal totalRevenue = orderJpaRepository.findAllByTenantId(tenantId).stream()
                .filter(o -> o.getPaymentStatus() == PaymentStatus.PAID)
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Subtract approved refunds from total revenue
        BigDecimal totalRefunds = refundJpaRepository.findAllByOrderTenantId(tenantId).stream()
                .filter(r -> r.getStatus() == RefundStatus.APPROVED)
                .map(r -> r.getRefundAmount() != null ? r.getRefundAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Net revenue = total revenue - approved refunds
        BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);
        
        long pendingOrders = orderJpaRepository.findAllByTenantId(tenantId).stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.PENDING)
                .count();
        
        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(netRevenue.longValue())
                .pendingOrders(pendingOrders)
                .totalCategories(totalCategories)
                .totalPromotions(totalPromotions)
                .totalReviews(totalReviews)
                .build();
    }
}

