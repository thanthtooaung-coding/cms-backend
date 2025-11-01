package com.content_management_system.bms.common.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Combo extends MasterData {
    private String comboName; // e.g. "Snack Combo"

    private double comboPrice; // sum of all selected foods

    private String photoUrl;

    @ManyToMany
    @JoinTable(name = "combo_foods", joinColumns = @JoinColumn(name = "combo_id"), inverseJoinColumns = @JoinColumn(name = "food_id"))
    private List<Food> foods = new ArrayList<>();
}
