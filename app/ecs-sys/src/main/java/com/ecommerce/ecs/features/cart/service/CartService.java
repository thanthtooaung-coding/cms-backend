package com.ecommerce.ecs.features.cart.service;

import com.ecommerce.ecs.features.cart.dto.AddToCartRequest;
import com.ecommerce.ecs.features.cart.dto.CartItemResponse;

import java.util.List;

public interface CartService {
    CartItemResponse addToCart(AddToCartRequest request, Long userId);
    List<CartItemResponse> getCart(Long userId);
    CartItemResponse updateQuantity(Long cartItemId, Integer quantity, Long userId);
    void removeFromCart(Long cartItemId, Long userId);
    void clearCart(Long userId);
}

