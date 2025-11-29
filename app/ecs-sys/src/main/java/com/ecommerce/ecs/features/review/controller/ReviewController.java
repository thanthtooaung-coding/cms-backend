package com.ecommerce.ecs.features.review.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.review.dto.CreateReviewRequest;
import com.ecommerce.ecs.features.review.dto.ReviewResponse;
import com.ecommerce.ecs.features.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @RequestBody CreateReviewRequest request,
            @RequestParam Long userId) {
        ReviewResponse response = reviewService.create(request, userId);
        return ApiResponse.created(response, "Review created successfully");
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByProduct(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getByProduct(productId);
        return ApiResponse.ok(reviews, "Reviews retrieved successfully");
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByUser(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getByUser(userId);
        return ApiResponse.ok(reviews, "User reviews retrieved successfully");
    }
    
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllByTenant(@PathVariable Long tenantId) {
        List<ReviewResponse> reviews = reviewService.getAllByTenant(tenantId);
        return ApiResponse.ok(reviews, "All reviews retrieved successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable Long id,
            @RequestBody CreateReviewRequest request,
            @RequestParam Long userId) {
        ReviewResponse response = reviewService.update(id, request, userId);
        return ApiResponse.ok(response, "Review updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestParam Long userId) {
        reviewService.delete(id, userId);
        return ApiResponse.noContent("Review deleted successfully");
    }
}

