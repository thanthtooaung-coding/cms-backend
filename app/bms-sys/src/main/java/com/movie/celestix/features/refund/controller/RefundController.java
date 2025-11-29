package com.movie.celestix.features.refund.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.common.jwt.JwtUtil;
import com.movie.celestix.features.refund.dto.RefundResponse;
import com.movie.celestix.features.refund.dto.UpdateRefundStatusRequest;
import com.movie.celestix.features.refund.service.RefundService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;
    private final JwtUtil jwtUtil;

    private String extractEmailFromToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwt = authorizationHeader.substring(7);
            return jwtUtil.extractUsername(jwt);
        }
        throw new RuntimeException("Authorization token is required");
    }

    @PostMapping("/request/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> requestRefund(
            @PathVariable Long bookingId,
            HttpServletRequest request
    ) {
        String email = extractEmailFromToken(request);
        refundService.requestRefund(bookingId, email);
        return ApiResponse.ok(null, "Refund requested successfully");
    }

    @PutMapping("/{refundId}")
    public ResponseEntity<ApiResponse<Void>> processRefund(
            @PathVariable Long refundId,
            @RequestBody UpdateRefundStatusRequest updateRequest,
            HttpServletRequest request
    ) {
        String email = extractEmailFromToken(request);
        refundService.processRefund(refundId, updateRequest, email);
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
