package com.movie.celestix.features.food.service;

import com.movie.celestix.common.enums.PaymentStatus;
import com.movie.celestix.common.models.*;
import com.movie.celestix.common.repository.jpa.*;
import com.movie.celestix.common.util.CreditCardValidator;
import com.movie.celestix.features.food.dto.CreateFoodOrderRequest;
import com.movie.celestix.features.food.dto.FoodOrderHistoryResponse;
import com.movie.celestix.features.food.dto.FoodOrderResponse;
import com.movie.celestix.features.food.mapper.FoodOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoodOrderServiceImpl implements FoodOrderService {

    private final FoodOrderJpaRepository foodOrderRepository;
    private final UserJpaRepository userRepository;
    private final FoodJpaRepository foodRepository;
    private final ComboJpaRepository comboRepository;
    private final FoodOrderMapper foodOrderMapper;

    @Override
    @Transactional
    public FoodOrderResponse createFoodOrder(CreateFoodOrderRequest request, String userEmail) {
        if (request.cardDetails() == null) {
            throw new IllegalArgumentException("Payment details are required.");
        }
        if (!CreditCardValidator.isValid(request.cardDetails().cardNumber())) {
            throw new IllegalArgumentException("Invalid credit card number provided.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FoodOrder foodOrder = new FoodOrder();
        foodOrder.setUser(user);

        Set<FoodOrderItem> orderItems = new HashSet<>();
        double totalPrice = 0.0;

        for (CreateFoodOrderRequest.OrderItemData item : request.items()) {
            String itemId = item.id();
            Integer quantity = item.quantity();

            FoodOrderItem orderItem = new FoodOrderItem();
            orderItem.setFoodOrder(foodOrder);
            orderItem.setQuantity(quantity);

            if ("food".equalsIgnoreCase(item.type())) {
                Food food = foodRepository.findById(Long.parseLong(itemId))
                        .orElseThrow(() -> new RuntimeException("Food not found with id: " + itemId));
                orderItem.setFood(food);
                orderItem.setPrice(BigDecimal.valueOf(food.getPrice()));
                totalPrice += food.getPrice() * quantity;
            } else if ("combo".equalsIgnoreCase(item.type())) {
                Combo combo = comboRepository.findById(Long.parseLong(itemId))
                        .orElseThrow(() -> new RuntimeException("Combo not found with id: " + itemId));
                orderItem.setCombo(combo);
                orderItem.setPrice(BigDecimal.valueOf(combo.getComboPrice()));
                totalPrice += combo.getComboPrice() * quantity;
            } else {
                throw new IllegalArgumentException("Invalid item type: " + item.type());
            }
            orderItems.add(orderItem);
        }

        foodOrder.setOrderItems(orderItems);
        foodOrder.setTotalPrice(BigDecimal.valueOf(totalPrice));

        // Payment Processing
        String header = "CELESTIX-" + user.getName() + request.cardDetails().cardNumber().substring(request.cardDetails().cardNumber().length() - 4);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("PAYMENT-INITIATOR", header);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("amount", totalPrice);
        body.put("currency", "USD");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        String paymentUrl = "https://aungkhaingkhant.online/test-bank/api/make-payment";
        Map<String, Object> paymentResponse = restTemplate.postForObject(paymentUrl, entity, Map.class);

        if (paymentResponse != null && "SUCCESS".equals(paymentResponse.get("status"))) {
            foodOrder.setPaymentStatus(PaymentStatus.SUCCESS);
        } else {
            foodOrder.setPaymentStatus(PaymentStatus.FAIL);
        }

        FoodOrder savedOrder = foodOrderRepository.save(foodOrder);
        return new FoodOrderResponse(savedOrder.getId(), savedOrder.getUser().getId(), savedOrder.getPaymentStatus(), savedOrder.getTotalPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodOrderHistoryResponse> getFoodOrderHistory(String userEmail) {
        List<FoodOrder> orders = foodOrderRepository.findByUserEmail(userEmail);
        return foodOrderMapper.toHistoryDtoList(orders);
    }
}