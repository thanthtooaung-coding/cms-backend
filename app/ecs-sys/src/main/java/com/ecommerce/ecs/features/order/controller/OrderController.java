package com.ecommerce.ecs.features.order.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.order.dto.CheckoutRequest;
import com.ecommerce.ecs.features.order.dto.OrderResponse;
import com.ecommerce.ecs.features.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @RequestBody CheckoutRequest request,
            @RequestParam Long userId,
            @RequestParam Long tenantId) {
        OrderResponse response = orderService.checkout(request, userId, tenantId);
        return ApiResponse.created(response, "Order placed successfully");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Long tenantId) {
        OrderResponse response = orderService.getById(id, userId, tenantId);
        return ApiResponse.ok(response, "Order retrieved successfully");
    }
    
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @RequestParam Long userId,
            @RequestParam Long tenantId) {
        List<OrderResponse> orders = orderService.getMyOrders(userId, tenantId);
        return ApiResponse.ok(orders, "Orders retrieved successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam Long tenantId) {
        List<OrderResponse> orders = orderService.getAllOrders(tenantId);
        return ApiResponse.ok(orders, "All orders retrieved successfully");
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam Long tenantId) {
        OrderResponse response = orderService.updateOrderStatus(id, status, tenantId);
        return ApiResponse.ok(response, "Order status updated successfully");
    }
    
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam Long tenantId) {
        OrderResponse response = orderService.updatePaymentStatus(id, status, tenantId);
        return ApiResponse.ok(response, "Payment status updated successfully");
    }
}

