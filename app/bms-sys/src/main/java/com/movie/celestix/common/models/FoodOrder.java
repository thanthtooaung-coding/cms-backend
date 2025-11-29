package com.movie.celestix.common.models;

import com.movie.celestix.common.converters.PaymentStatusConverter;
import com.movie.celestix.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "food_orders")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FoodOrder extends MasterData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "foodOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FoodOrderItem> orderItems = new HashSet<>();

    private BigDecimal totalPrice;
}
