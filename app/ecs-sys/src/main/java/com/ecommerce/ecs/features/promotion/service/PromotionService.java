package com.ecommerce.ecs.features.promotion.service;

import com.ecommerce.ecs.features.promotion.dto.PromotionRequest;
import com.ecommerce.ecs.features.promotion.dto.PromotionResponse;

import java.util.List;

public interface PromotionService {
    PromotionResponse create(PromotionRequest request, Long tenantId);
    List<PromotionResponse> getAll(Long tenantId);
    PromotionResponse getById(Long id, Long tenantId);
    PromotionResponse getByCode(String code, Long tenantId);
    PromotionResponse update(Long id, PromotionRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}

