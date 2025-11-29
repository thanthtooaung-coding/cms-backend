package com.ecommerce.ecs.features.refund.service;

import com.ecommerce.ecs.features.refund.dto.RefundResponse;
import com.ecommerce.ecs.features.refund.dto.RequestRefundRequest;
import com.ecommerce.ecs.features.refund.dto.UpdateRefundStatusRequest;

import java.util.List;

public interface RefundService {
    RefundResponse requestRefund(RequestRefundRequest request, Long userId);
    List<RefundResponse> getMyRefunds(Long userId);
    List<RefundResponse> getAllRefunds(Long tenantId);
    RefundResponse updateStatus(Long id, UpdateRefundStatusRequest request, Long approvedBy, Long tenantId);
}

