package com.ecommerce.ecs.features.review.service.impl;

import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Product;
import com.ecommerce.ecs.common.models.Review;
import com.ecommerce.ecs.common.models.User;
import com.ecommerce.ecs.common.repository.jpa.ProductJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.ReviewJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.UserJpaRepository;
import com.ecommerce.ecs.features.review.dto.CreateReviewRequest;
import com.ecommerce.ecs.features.review.dto.ReviewResponse;
import com.ecommerce.ecs.features.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewJpaRepository reviewJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    
    @Override
    @Transactional
    public ReviewResponse create(CreateReviewRequest request, Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Product product = productJpaRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review saved = reviewJpaRepository.save(review);
        return toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getByProduct(Long productId) {
        return reviewJpaRepository.findAllByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getByUser(Long userId) {
        return reviewJpaRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllByTenant(Long tenantId) {
        return reviewJpaRepository.findAllByProductTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ReviewResponse update(Long id, CreateReviewRequest request, Long userId) {
        Review review = reviewJpaRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review updated = reviewJpaRepository.save(review);
        return toResponse(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Review review = reviewJpaRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        reviewJpaRepository.delete(review);
    }
    
    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}

