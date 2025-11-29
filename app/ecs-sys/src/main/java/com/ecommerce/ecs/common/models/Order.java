package com.ecommerce.ecs.common.models;

import com.ecommerce.ecs.common.converters.OrderStatusConverter;
import com.ecommerce.ecs.common.converters.PaymentStatusConverter;
import com.ecommerce.ecs.common.enums.OrderStatus;
import com.ecommerce.ecs.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE orders SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Order extends MasterData {
    
    @Column(name = "order_number", unique = true)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    
    @Convert(converter = PaymentStatusConverter.class)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;
    
    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "shipping_address", length = 1024)
    private String shippingAddress;
    
    @Column(name = "billing_address", length = 1024)
    private String billingAddress;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new java.util.ArrayList<>();
    
    @OneToMany(mappedBy = "order")
    private List<Refund> refunds = new java.util.ArrayList<>();
}

