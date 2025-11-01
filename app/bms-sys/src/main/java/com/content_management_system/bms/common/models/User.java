package com.content_management_system.bms.common.models;

import com.content_management_system.bms.common.converters.RoleConverter;
import com.content_management_system.bms.common.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
}
