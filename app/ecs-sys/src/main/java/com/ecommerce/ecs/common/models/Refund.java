package com.ecommerce.ecs.common.models;

import com.ecommerce.ecs.common.converters.RefundStatusConverter;
import com.ecommerce.ecs.common.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Table(name = "refunds")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE refunds SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Refund extends MasterData {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Convert(converter = RefundStatusConverter.class)
    private RefundStatus status;
    
    @Column(name = "refund_amount", precision = 19, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "reason", length = 1024)
    private String reason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}

