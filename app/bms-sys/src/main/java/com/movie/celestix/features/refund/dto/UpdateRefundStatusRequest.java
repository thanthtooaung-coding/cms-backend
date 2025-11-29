package com.movie.celestix.features.refund.dto;

import com.movie.celestix.common.enums.RefundStatus;

public record UpdateRefundStatusRequest(
    RefundStatus status
) {}
