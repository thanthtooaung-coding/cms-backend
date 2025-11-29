package com.ecommerce.ecs.common.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configurations")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Configuration extends MasterData {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}

