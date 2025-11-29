package com.movie.celestix.common.models;

import com.movie.celestix.common.converters.RoleConverter;
import com.movie.celestix.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
