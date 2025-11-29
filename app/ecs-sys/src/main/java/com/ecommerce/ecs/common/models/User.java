package com.ecommerce.ecs.common.models;

import com.ecommerce.ecs.common.converters.RoleConverter;
import com.ecommerce.ecs.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User extends MasterData {

    private String name;

    private String password;

    @Column(unique = true)
    private String email;

    @Convert(converter = RoleConverter.class)
    private Role role;

    private String profileUrl;

    private String otp;

    private LocalDateTime otpGeneratedTime;

    private String address;

    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<CartItem> cartItems = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new java.util.ArrayList<>();
}

