package com.ecommerce.ecs.features.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    @NotBlank
    private String shippingAddress;
    
    @NotBlank
    private String billingAddress;
    
    @NotBlank
    private String paymentMethod;
    
    private String promotionCode;
    
    private List<Long> cartItemIds; // Optional: specific cart items to checkout
}

