package com.ecommerce.ecs.features.cart.service.impl;

import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.CartItem;
import com.ecommerce.ecs.common.models.Product;
import com.ecommerce.ecs.common.models.User;
import com.ecommerce.ecs.common.repository.jpa.CartItemJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.ProductJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.UserJpaRepository;
import com.ecommerce.ecs.features.cart.dto.AddToCartRequest;
import com.ecommerce.ecs.features.cart.dto.CartItemResponse;
import com.ecommerce.ecs.features.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartItemJpaRepository cartItemJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final UserJpaRepository userJpaRepository;
    
    @Override
    @Transactional
    public CartItemResponse addToCart(AddToCartRequest request, Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Product product = productJpaRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock");
        }
        
        CartItem existingItem = cartItemJpaRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            CartItem saved = cartItemJpaRepository.save(existingItem);
            return toResponse(saved);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            CartItem saved = cartItemJpaRepository.save(newItem);
            return toResponse(saved);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        return cartItemJpaRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CartItemResponse updateQuantity(Long cartItemId, Integer quantity, Long userId) {
        CartItem cartItem = cartItemJpaRepository.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        if (cartItem.getProduct().getStock() < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        
        cartItem.setQuantity(quantity);
        CartItem saved = cartItemJpaRepository.save(cartItem);
        return toResponse(saved);
    }
    
    @Override
    @Transactional
    public void removeFromCart(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemJpaRepository.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemJpaRepository.delete(cartItem);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemJpaRepository.deleteAllByUserId(userId);
    }
    
    private CartItemResponse toResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .productPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }
}

