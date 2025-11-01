package com.content_management_system.bms.common.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "food_categories")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FoodCategory extends MasterData {
    private String name;

    private String description;
}
