package com.content_management_system.bms.common.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movie_genres")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MovieGenre extends MasterData {
    private String name;

    private String description;
}
