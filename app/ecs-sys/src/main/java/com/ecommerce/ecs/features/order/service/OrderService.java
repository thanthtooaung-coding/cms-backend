package com.ecommerce.ecs.features.order.service;

import com.ecommerce.ecs.features.order.dto.CheckoutRequest;
import com.ecommerce.ecs.features.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(CheckoutRequest request, Long userId, Long tenantId);
    OrderResponse getById(Long id, Long userId, Long tenantId);
    List<OrderResponse> getMyOrders(Long userId, Long tenantId);
    List<OrderResponse> getAllOrders(Long tenantId);
    OrderResponse updateOrderStatus(Long id, String status, Long tenantId);
    OrderResponse updatePaymentStatus(Long id, String status, Long tenantId);
}

