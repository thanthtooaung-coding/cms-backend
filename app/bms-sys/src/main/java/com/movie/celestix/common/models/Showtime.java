package com.movie.celestix.common.models;

import com.movie.celestix.common.converters.ShowtimeStatusConverter;
import com.movie.celestix.common.enums.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "showtimes")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SQLDelete(sql = "UPDATE showtimes SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Showtime extends MasterData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "showtime_date", nullable = false)
    private LocalDate showtimeDate;

    @Column(name = "showtime_time", nullable = false)
    private LocalTime showtimeTime;

    @Column(name = "seats_available", nullable = false)
    private Integer seatsAvailable;

    @Convert(converter = ShowtimeStatusConverter.class)
    private ShowtimeStatus status;

    @OneToMany(mappedBy = "showtime")
    private Set<BookedSeat> bookedSeats = new HashSet<>();
}
