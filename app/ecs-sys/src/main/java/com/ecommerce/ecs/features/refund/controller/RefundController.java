package com.ecommerce.ecs.features.refund.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.refund.dto.RefundResponse;
import com.ecommerce.ecs.features.refund.dto.RequestRefundRequest;
import com.ecommerce.ecs.features.refund.dto.UpdateRefundStatusRequest;
import com.ecommerce.ecs.features.refund.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
public class RefundController {
    
    private final RefundService refundService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<RefundResponse>> requestRefund(
            @RequestBody RequestRefundRequest request,
            @RequestParam Long userId) {
        RefundResponse response = refundService.requestRefund(request, userId);
        return ApiResponse.created(response, "Refund requested successfully");
    }
    
    @GetMapping("/my-refunds")
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getMyRefunds(@RequestParam Long userId) {
        List<RefundResponse> refunds = refundService.getMyRefunds(userId);
        return ApiResponse.ok(refunds, "Refunds retrieved successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getAllRefunds(@RequestParam Long tenantId) {
        List<RefundResponse> refunds = refundService.getAllRefunds(tenantId);
        return ApiResponse.ok(refunds, "All refunds retrieved successfully");
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RefundResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateRefundStatusRequest request,
            @RequestParam Long approvedBy,
            @RequestParam Long tenantId) {
        RefundResponse response = refundService.updateStatus(id, request, approvedBy, tenantId);
        return ApiResponse.ok(response, "Refund status updated successfully");
    }
}

