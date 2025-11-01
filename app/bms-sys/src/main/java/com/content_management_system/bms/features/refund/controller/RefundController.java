package com.content_management_system.bms.features.refund.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.refund.dto.RefundResponse;
import com.content_management_system.bms.features.refund.dto.UpdateRefundStatusRequest;
import com.content_management_system.bms.features.refund.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/request/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> requestRefund(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        refundService.requestRefund(bookingId, userDetails.getUsername());
        return ApiResponse.ok(null, "Refund requested successfully");
    }

    @PutMapping("/{refundId}")
    public ResponseEntity<ApiResponse<Void>> processRefund(
            @PathVariable Long refundId,
            @RequestBody UpdateRefundStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        refundService.processRefund(refundId, request, userDetails.getUsername());
        return ApiResponse.ok(null, "Refund status updated successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getAllRefunds() {
        return ApiResponse.ok(refundService.getAllRefunds(), "Refunds retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RefundResponse>> getRefundById(@PathVariable Long id) {
        return ApiResponse.ok(refundService.getRefundById(id), "Refund retrieved successfully");
    }
}
