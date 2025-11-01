package com.content_management_system.bms.common.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String value;
}
