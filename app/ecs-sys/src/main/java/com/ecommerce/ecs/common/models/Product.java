package com.ecommerce.ecs.common.models;

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
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE products SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Product extends MasterData {
    
    private String name;
    
    @Column(length = 2048)
    private String description;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private String sku;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "is_active")
    private boolean isActive;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new java.util.ArrayList<>();
    
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems = new java.util.ArrayList<>();
    
    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new java.util.ArrayList<>();
}

