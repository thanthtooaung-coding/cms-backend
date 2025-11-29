package com.movie.celestix.features.food.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.common.jwt.JwtUtil;
import com.movie.celestix.features.food.dto.CreateFoodOrderRequest;
import com.movie.celestix.features.food.dto.FoodOrderHistoryResponse;
import com.movie.celestix.features.food.dto.FoodOrderResponse;
import com.movie.celestix.features.food.service.FoodOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food-orders")
@RequiredArgsConstructor
public class FoodOrderController {

    private final FoodOrderService foodOrderService;
    private final JwtUtil jwtUtil;

    private String extractEmailFromToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwt = authorizationHeader.substring(7);
            return jwtUtil.extractUsername(jwt);
        }
        throw new RuntimeException("Authorization token is required");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodOrderResponse>> createFoodOrder(
            @RequestBody CreateFoodOrderRequest request,
            HttpServletRequest httpRequest
    ) {
        String email = extractEmailFromToken(httpRequest);
        FoodOrderResponse order = foodOrderService.createFoodOrder(request, email);
        return ApiResponse.created(order, "Food order created successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<FoodOrderHistoryResponse>>> getMyFoodOrders(
            HttpServletRequest request
    ) {
        String email = extractEmailFromToken(request);
        List<FoodOrderHistoryResponse> history = foodOrderService.getFoodOrderHistory(email);
        return ApiResponse.ok(history, "Food order history retrieved successfully");
    }
}
