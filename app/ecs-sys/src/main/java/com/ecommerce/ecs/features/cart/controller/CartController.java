package com.ecommerce.ecs.features.cart.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.cart.dto.AddToCartRequest;
import com.ecommerce.ecs.features.cart.dto.CartItemResponse;
import com.ecommerce.ecs.features.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @RequestBody AddToCartRequest request,
            @RequestParam Long userId) {
        CartItemResponse response = cartService.addToCart(request, userId);
        return ApiResponse.created(response, "Item added to cart successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(@RequestParam Long userId) {
        List<CartItemResponse> cart = cartService.getCart(userId);
        return ApiResponse.ok(cart, "Cart retrieved successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestParam Long userId) {
        CartItemResponse response = cartService.updateQuantity(id, quantity, userId);
        return ApiResponse.ok(response, "Cart item updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Long id,
            @RequestParam Long userId) {
        cartService.removeFromCart(id, userId);
        return ApiResponse.noContent("Item removed from cart successfully");
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.noContent("Cart cleared successfully");
    }
}

