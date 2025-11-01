package com.content_management_system.bms.common.models;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Embeddable
public class SeatInfo {
    private int totalRows;
    private BigDecimal totalPrice;
}
