package com.ecommerce.ecs.features.review.service;

import com.ecommerce.ecs.features.review.dto.CreateReviewRequest;
import com.ecommerce.ecs.features.review.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request, Long userId);
    List<ReviewResponse> getByProduct(Long productId);
    List<ReviewResponse> getByUser(Long userId);
    List<ReviewResponse> getAllByTenant(Long tenantId);
    ReviewResponse update(Long id, CreateReviewRequest request, Long userId);
    void delete(Long id, Long userId);
}

