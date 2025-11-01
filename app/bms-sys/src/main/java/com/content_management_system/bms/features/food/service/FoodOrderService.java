package com.content_management_system.bms.features.food.service;

import com.content_management_system.bms.features.food.dto.CreateFoodOrderRequest;
import com.content_management_system.bms.features.food.dto.FoodOrderHistoryResponse;
import com.content_management_system.bms.features.food.dto.FoodOrderResponse;

import java.util.List;

public interface FoodOrderService {
    FoodOrderResponse createFoodOrder(CreateFoodOrderRequest request, String userEmail);
    List<FoodOrderHistoryResponse> getFoodOrderHistory(String userEmail);
}
