package com.ecommerce.ecs.common.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "tenants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE tenants SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Tenant extends MasterData {
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "tenant")
    private List<User> users = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "tenant")
    private List<Product> products = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "tenant")
    private List<Category> categories = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "tenant")
    private List<Order> orders = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "tenant")
    private List<Promotion> promotions = new java.util.ArrayList<>();
}

