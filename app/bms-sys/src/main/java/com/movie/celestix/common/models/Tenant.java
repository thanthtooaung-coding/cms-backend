package com.movie.celestix.common.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private List<User> users;

    @OneToMany(mappedBy = "tenant")
    private List<Movie> movies;

    @OneToMany(mappedBy = "tenant")
    private List<Theater> theaters;

    @OneToMany(mappedBy = "tenant")
    private List<Food> foods;

    @OneToMany(mappedBy = "tenant")
    private List<Combo> combos;

    @OneToMany(mappedBy = "tenant")
    private List<Configuration> configurations;

    @OneToMany(mappedBy = "tenant")
    private List<MovieGenre> movieGenres;

    @OneToMany(mappedBy = "tenant")
    private List<FoodCategory> foodCategories;
}

