package com.content_management_system.bms.common.models;

import com.content_management_system.bms.common.converters.MovieLanguageConverter;
import com.content_management_system.bms.common.converters.MovieRatingConverter;
import com.content_management_system.bms.common.converters.MovieStatusConverter;
import com.content_management_system.bms.common.enums.MovieLanguage;
import com.content_management_system.bms.common.enums.MovieRating;
import com.content_management_system.bms.common.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SQLDelete(sql = "UPDATE movies SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Movie extends MasterData {

    private String title;

    @Column(length = 2048)
    private String description;

    private String duration;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Convert(converter = MovieRatingConverter.class)
    private MovieRating rating;

    @Convert(converter = MovieLanguageConverter.class)
    private MovieLanguage language;

    private String director;

    @Column(name = "movie_cast", length = 1024)
    private String movieCast;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "movie_poster_url")
    private String moviePosterUrl;

    @Convert(converter = MovieStatusConverter.class)
    private MovieStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_has_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<MovieGenre> genres = new HashSet<>();
}
