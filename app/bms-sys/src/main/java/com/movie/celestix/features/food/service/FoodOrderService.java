package com.movie.celestix.features.food.service;

import com.movie.celestix.features.food.dto.CreateFoodOrderRequest;
import com.movie.celestix.features.food.dto.FoodOrderHistoryResponse;
import com.movie.celestix.features.food.dto.FoodOrderResponse;

import java.util.List;

public interface FoodOrderService {
    FoodOrderResponse createFoodOrder(CreateFoodOrderRequest request, String userEmail);
    List<FoodOrderHistoryResponse> getFoodOrderHistory(String userEmail);
}
