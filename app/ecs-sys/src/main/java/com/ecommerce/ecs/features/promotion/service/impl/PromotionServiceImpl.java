package com.ecommerce.ecs.features.promotion.service.impl;

import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Promotion;
import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.PromotionJpaRepository;
import com.ecommerce.ecs.features.promotion.dto.PromotionRequest;
import com.ecommerce.ecs.features.promotion.dto.PromotionResponse;
import com.ecommerce.ecs.features.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    
    private final PromotionJpaRepository promotionJpaRepository;
    private final TenantRepository tenantRepository;
    
    @Override
    @Transactional
    public PromotionResponse create(PromotionRequest request, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        if (promotionJpaRepository.findByCodeAndTenantId(request.getCode(), tenantId).isPresent()) {
            throw new IllegalStateException("Promotion code already exists");
        }
        
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setCode(request.getCode());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinPurchaseAmount(request.getMinPurchaseAmount());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.isActive());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsedCount(0);
        promotion.setTenant(tenant);
        
        Promotion saved = promotionJpaRepository.save(promotion);
        return toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getAll(Long tenantId) {
        return promotionJpaRepository.findAllByTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getById(Long id, Long tenantId) {
        Promotion promotion = promotionJpaRepository.findById(id)
                .filter(p -> p.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        return toResponse(promotion);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getByCode(String code, Long tenantId) {
        Promotion promotion = promotionJpaRepository.findByCodeAndTenantId(code, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        return toResponse(promotion);
    }
    
    @Override
    @Transactional
    public PromotionResponse update(Long id, PromotionRequest request, Long tenantId) {
        Promotion promotion = promotionJpaRepository.findById(id)
                .filter(p -> p.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        
        promotion.setName(request.getName());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinPurchaseAmount(request.getMinPurchaseAmount());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.isActive());
        promotion.setUsageLimit(request.getUsageLimit());
        
        Promotion updated = promotionJpaRepository.save(promotion);
        return toResponse(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long tenantId) {
        Promotion promotion = promotionJpaRepository.findById(id)
                .filter(p -> p.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        promotionJpaRepository.delete(promotion);
    }
    
    private PromotionResponse toResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .code(promotion.getCode())
                .promotionType(promotion.getPromotionType())
                .discountValue(promotion.getDiscountValue())
                .minPurchaseAmount(promotion.getMinPurchaseAmount())
                .maxDiscountAmount(promotion.getMaxDiscountAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.isActive())
                .usageLimit(promotion.getUsageLimit())
                .usedCount(promotion.getUsedCount())
                .tenantId(promotion.getTenant().getId())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}

