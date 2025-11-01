package com.content_management_system.bms.features.refund.service;

import com.content_management_system.bms.features.refund.dto.RefundResponse;
import com.content_management_system.bms.features.refund.dto.UpdateRefundStatusRequest;

import java.util.List;

public interface RefundService {
    void requestRefund(Long bookingId, String userEmail);
    void processRefund(Long refundId, UpdateRefundStatusRequest request, String adminEmail);
    List<RefundResponse> getAllRefunds();
    RefundResponse getRefundById(Long id);
}
