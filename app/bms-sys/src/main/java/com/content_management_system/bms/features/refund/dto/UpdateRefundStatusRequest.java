package com.content_management_system.bms.features.refund.dto;

import com.content_management_system.bms.common.enums.RefundStatus;

public record UpdateRefundStatusRequest(
    RefundStatus status
) {}
