package com.ecommerce.ecs.features.promotion.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.promotion.dto.PromotionRequest;
import com.ecommerce.ecs.features.promotion.dto.PromotionResponse;
import com.ecommerce.ecs.features.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {
    
    private final PromotionService promotionService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponse>> create(
            @RequestBody PromotionRequest request,
            @RequestParam Long tenantId) {
        PromotionResponse response = promotionService.create(request, tenantId);
        return ApiResponse.created(response, "Promotion created successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAll(@RequestParam Long tenantId) {
        List<PromotionResponse> promotions = promotionService.getAll(tenantId);
        return ApiResponse.ok(promotions, "Promotions retrieved successfully");
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PromotionResponse>> getByCode(
            @PathVariable String code,
            @RequestParam Long tenantId) {
        PromotionResponse response = promotionService.getByCode(code, tenantId);
        return ApiResponse.ok(response, "Promotion retrieved successfully");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> getById(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        PromotionResponse response = promotionService.getById(id, tenantId);
        return ApiResponse.ok(response, "Promotion retrieved successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> update(
            @PathVariable Long id,
            @RequestBody PromotionRequest request,
            @RequestParam Long tenantId) {
        PromotionResponse response = promotionService.update(id, request, tenantId);
        return ApiResponse.ok(response, "Promotion updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        promotionService.delete(id, tenantId);
        return ApiResponse.noContent("Promotion deleted successfully");
    }
}

