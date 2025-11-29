package com.ecommerce.ecs.features.order.dto;

import com.ecommerce.ecs.common.enums.OrderStatus;
import com.ecommerce.ecs.common.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private Long promotionId;
    private String promotionCode;
    private List<OrderItemResponse> orderItems;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

