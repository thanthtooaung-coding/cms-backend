package com.movie.celestix.common.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class SeatConfiguration {
    @Column(name = "seat_row")
    private int row;

    @Column(name = "seat_column")
    private int column;
}
