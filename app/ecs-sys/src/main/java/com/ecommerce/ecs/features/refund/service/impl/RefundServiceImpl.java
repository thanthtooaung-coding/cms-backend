package com.ecommerce.ecs.features.refund.service.impl;

import com.ecommerce.ecs.common.enums.RefundStatus;
import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Order;
import com.ecommerce.ecs.common.models.Refund;
import com.ecommerce.ecs.common.models.User;
import com.ecommerce.ecs.common.repository.jpa.OrderJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.RefundJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.UserJpaRepository;
import com.ecommerce.ecs.features.refund.dto.RefundResponse;
import com.ecommerce.ecs.features.refund.dto.RequestRefundRequest;
import com.ecommerce.ecs.features.refund.dto.UpdateRefundStatusRequest;
import com.ecommerce.ecs.features.refund.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    
    private final RefundJpaRepository refundJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final UserJpaRepository userJpaRepository;
    
    @Override
    @Transactional
    public RefundResponse requestRefund(RequestRefundRequest request, Long userId) {
        Order order = orderJpaRepository.findById(request.getOrderId())
                .filter(o -> o.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (refundJpaRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException("Refund already requested for this order");
        }
        
        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setStatus(RefundStatus.PENDING);
        refund.setReason(request.getReason());
        refund.setRefundAmount(request.getRefundAmount() != null ? 
                request.getRefundAmount() : order.getTotalAmount());
        
        Refund saved = refundJpaRepository.save(refund);
        return toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getMyRefunds(Long userId) {
        return refundJpaRepository.findAll().stream()
                .filter(r -> r.getOrder().getUser().getId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getAllRefunds(Long tenantId) {
        return refundJpaRepository.findAllByOrderTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public RefundResponse updateStatus(Long id, UpdateRefundStatusRequest request, Long approvedBy, Long tenantId) {
        Refund refund = refundJpaRepository.findById(id)
                .filter(r -> r.getOrder().getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));
        
        refund.setStatus(request.getStatus());
        
        if (approvedBy != null) {
            User approver = userJpaRepository.findById(approvedBy)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            refund.setApprovedBy(approver);
        }
        
        Refund updated = refundJpaRepository.save(refund);
        return toResponse(updated);
    }
    
    private RefundResponse toResponse(Refund refund) {
        return RefundResponse.builder()
                .id(refund.getId())
                .orderId(refund.getOrder().getId())
                .orderNumber(refund.getOrder().getOrderNumber())
                .status(refund.getStatus())
                .refundAmount(refund.getRefundAmount())
                .reason(refund.getReason())
                .approvedBy(refund.getApprovedBy() != null ? refund.getApprovedBy().getId() : null)
                .approvedByName(refund.getApprovedBy() != null ? refund.getApprovedBy().getName() : null)
                .createdAt(refund.getCreatedAt())
                .updatedAt(refund.getUpdatedAt())
                .build();
    }
}

