package com.ecommerce.ecs.features.order.service.impl;

import com.ecommerce.ecs.common.enums.OrderStatus;
import com.ecommerce.ecs.common.enums.PaymentStatus;
import com.ecommerce.ecs.common.enums.PromotionType;
import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.*;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.*;
import com.ecommerce.ecs.features.order.dto.CheckoutRequest;
import com.ecommerce.ecs.features.order.dto.OrderItemResponse;
import com.ecommerce.ecs.features.order.dto.OrderResponse;
import com.ecommerce.ecs.features.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final CartItemJpaRepository cartItemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final TenantRepository tenantRepository;
    private final PromotionJpaRepository promotionJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    
    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request, Long userId, Long tenantId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        List<CartItem> cartItems = cartItemJpaRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setUser(user);
        order.setTenant(tenant);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();
        
        // Process cart items
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            
            // Update stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productJpaRepository.save(product);
        }
        
        order.setOrderItems(orderItems);
        
        // Apply promotion if provided
        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            Promotion promotion = promotionJpaRepository.findByCodeAndTenantId(request.getPromotionCode(), tenantId)
                    .orElse(null);
            
            if (promotion != null && promotion.isActive() && 
                promotion.getStartDate().isBefore(LocalDateTime.now()) &&
                promotion.getEndDate().isAfter(LocalDateTime.now()) &&
                (promotion.getUsageLimit() == null || promotion.getUsedCount() < promotion.getUsageLimit())) {
                
                BigDecimal discount = BigDecimal.ZERO;
                if (promotion.getPromotionType() == PromotionType.PERCENTAGE_DISCOUNT) {
                    discount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
                    if (promotion.getMaxDiscountAmount() != null && discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                        discount = promotion.getMaxDiscountAmount();
                    }
                } else if (promotion.getPromotionType() == PromotionType.FIXED_DISCOUNT) {
                    discount = promotion.getDiscountValue();
                }
                
                if (promotion.getMinPurchaseAmount() == null || totalAmount.compareTo(promotion.getMinPurchaseAmount()) >= 0) {
                    totalAmount = totalAmount.subtract(discount);
                    order.setPromotion(promotion);
                    promotion.setUsedCount(promotion.getUsedCount() + 1);
                    promotionJpaRepository.save(promotion);
                }
            }
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderJpaRepository.save(order);
        
        // Save order items
        for (OrderItem item : orderItems) {
            orderItemJpaRepository.save(item);
        }
        
        // Clear cart
        cartItemJpaRepository.deleteAllByUserId(userId);
        
        return toResponse(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id, Long userId, Long tenantId) {
        Order order = orderJpaRepository.findById(id)
                .filter(o -> o.getUser().getId().equals(userId) && o.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toResponse(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId, Long tenantId) {
        return orderJpaRepository.findAllByUserId(userId).stream()
                .filter(o -> o.getTenant().getId().equals(tenantId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(Long tenantId) {
        return orderJpaRepository.findAllByTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status, Long tenantId) {
        Order order = orderJpaRepository.findById(id)
                .filter(o -> o.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order updated = orderJpaRepository.save(order);
        return toResponse(updated);
    }
    
    @Override
    @Transactional
    public OrderResponse updatePaymentStatus(Long id, String status, Long tenantId) {
        Order order = orderJpaRepository.findById(id)
                .filter(o -> o.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setPaymentStatus(PaymentStatus.valueOf(status.toUpperCase()));
        Order updated = orderJpaRepository.save(order);
        return toResponse(updated);
    }
    
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = (order.getOrderItems() != null ? order.getOrderItems() : 
                orderItemJpaRepository.findAllByOrderId(order.getId())).stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentMethod(order.getPaymentMethod())
                .promotionId(order.getPromotion() != null ? order.getPromotion().getId() : null)
                .promotionCode(order.getPromotion() != null ? order.getPromotion().getCode() : null)
                .orderItems(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

