package com.content_management_system.bms.features.movies.service.impl;

import com.content_management_system.bms.common.enums.MovieLanguage;
import com.content_management_system.bms.common.enums.MovieRating;
import com.content_management_system.bms.common.enums.MovieStatus;
import com.content_management_system.bms.common.models.Movie;
import com.content_management_system.bms.common.models.MovieGenre;
import com.content_management_system.bms.common.models.Showtime;
import com.content_management_system.bms.common.repository.jpa.BookedSeatJpaRepository;
import com.content_management_system.bms.common.repository.jpa.MovieGenreJpaRepository;
import com.content_management_system.bms.common.repository.jpa.MovieJpaRepository;
import com.content_management_system.bms.common.repository.jpa.ShowtimeJpaRepository;
import com.content_management_system.bms.features.moviegenres.dto.MovieGenreResponse;
import com.content_management_system.bms.features.movies.dto.CreateMovieRequest;
import com.content_management_system.bms.features.movies.dto.MovieResponse;
import com.content_management_system.bms.features.movies.dto.MovieTemplateResponse;
import com.content_management_system.bms.features.movies.dto.UpdateMovieRequest;
import com.content_management_system.bms.features.movies.mapper.MovieMapper;
import com.content_management_system.bms.features.movies.service.MovieEnumService;
import com.content_management_system.bms.features.movies.service.MovieService;
import com.content_management_system.bms.features.publicroutes.dto.PopularMovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieJpaRepository movieJpaRepository;
    private final MovieGenreJpaRepository movieGenreJpaRepository;
    private final MovieMapper movieMapper;
    private final MovieEnumService movieEnumService;
    private final ShowtimeJpaRepository showtimeJpaRepository;
    private final BookedSeatJpaRepository bookedSeatJpaRepository;


    @Override
    @Transactional
    public MovieResponse create(CreateMovieRequest request) {
        final Movie movie = movieMapper.toEntity(request);
        movie.setRating(getRatingFromString(request.rating()));
        movie.setLanguage(getLanguageFromString(request.language()));
        movie.setStatus(getStatusFromString(request.status()));
        return getMovieResponse(movie, request.genreIds());
    }

    private MovieResponse getMovieResponse(final Movie movie, final Set<Long> genresIds) {
        final Set<MovieGenre> genres = new HashSet<>(movieGenreJpaRepository.findAllById(genresIds));
        if (genres.size() != genresIds.size()) {
            throw new RuntimeException("One or more genres not found");
        }
        movie.setGenres(genres);
        final Movie savedMovie = movieJpaRepository.save(movie);
        return movieMapper.toDto(savedMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse retrieveOne(Long id) {
        final Movie movie = movieJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie with id " + id + " not found"));
        return movieMapper.toDto(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAll() {
        return movieJpaRepository.findAll().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieResponse update(Long id, UpdateMovieRequest request) {
        final Movie movie = movieJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie with id " + id + " not found"));
        movieMapper.updateMovieFromDto(request, movie);

        if (request.rating() != null) {
            movie.setRating(getRatingFromString(request.rating()));
        }
        if (request.language() != null) {
            movie.setLanguage(getLanguageFromString(request.language()));
        }
        if (request.status() != null) {
            movie.setStatus(getStatusFromString(request.status()));
        }

        return getMovieResponse(movie, request.genreIds());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!movieJpaRepository.existsById(id)) {
            throw new RuntimeException("Movie with id " + id + " not found");
        }
        if (showtimeJpaRepository.existsByMovieId(id)) {
            throw new IllegalStateException("Cannot delete movie with id " + id + " because it is associated with one or more showtimes");
        }
        movieJpaRepository.deleteById(id);
    }

    @Override
    public MovieTemplateResponse getMovieTemplate() {
        return new MovieTemplateResponse(
                movieEnumService.getAllRatings(),
                movieEnumService.getAllLanguages(),
                movieEnumService.getAllStatuses()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAllByStatus(String status) {
        MovieStatus movieStatus = getStatusFromString(status);
        return movieJpaRepository.findAllByStatus(movieStatus).stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAvailableMovies() {
        final LocalDate today = LocalDate.now();
        final LocalDate twoDaysFromNow = today.plusDays(2);
        final List<Movie> moviesWithShowtimes = showtimeJpaRepository.findAll().stream()
                .filter(showtime -> !showtime.getShowtimeDate().isBefore(today) && !showtime.getShowtimeDate().isAfter(twoDaysFromNow))
                .map(Showtime::getMovie)
                .distinct()
                .toList();

        return moviesWithShowtimes.stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAllAvailableMoviesByStatus(String status) {
        final MovieStatus movieStatus = getStatusFromString(status);
        final List<Movie> moviesByStatus = movieJpaRepository.findAllByStatus(movieStatus);
        final LocalDate today = LocalDate.now();
        final LocalDate twoDaysFromNow = today.plusDays(2);

        if (movieStatus == MovieStatus.COMING_SOON) {
            /*final List<Long> movieIdsWithFutureShowtimes = showtimeJpaRepository.findAll().stream()
                    .filter(showtime -> showtime.getShowtimeDate().isAfter(twoDaysFromNow))
                    .map(showtime -> showtime.getMovie().getId())
                    .distinct()
                    .toList();

            return moviesByStatus.stream()
                    .filter(movie -> movieIdsWithFutureShowtimes.contains(movie.getId()))
                    .map(movieMapper::toDto)
                    .collect(Collectors.toList());*/
            return moviesByStatus.stream()
                    .filter(movie -> movie.getReleaseDate() != null && movie.getReleaseDate().isAfter(twoDaysFromNow))
                    .map(movieMapper::toDto)
                    .collect(Collectors.toList());
        } else { // NOW_SHOWING
            final List<Long> movieIdsWithShowtimes = showtimeJpaRepository.findAll().stream()
                    .filter(showtime -> !showtime.getShowtimeDate().isBefore(today) && !showtime.getShowtimeDate().isAfter(twoDaysFromNow))
                    .map(showtime -> showtime.getMovie().getId())
                    .distinct()
                    .toList();

            return moviesByStatus.stream()
                    .filter(movie -> movieIdsWithShowtimes.contains(movie.getId()))
                    .map(movieMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PopularMovieResponse> retrievePopularMovies() {
        List<Movie> availableMovies = retrieveAvailableMovies().stream()
                .map(movieResponse -> movieJpaRepository.findById(movieResponse.id()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<BookedSeatJpaRepository.MovieBookingCount> bookingCounts = bookedSeatJpaRepository.countBookingsByMovie();

        Map<Long, Long> bookingCountMap = bookingCounts.stream()
                .collect(Collectors.toMap(
                        BookedSeatJpaRepository.MovieBookingCount::getMovieId,
                        BookedSeatJpaRepository.MovieBookingCount::getBookingCount
                ));

        long maxBookings = bookingCountMap.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(1L);

        return availableMovies.stream()
                .map(movie -> {
                    long count = bookingCountMap.getOrDefault(movie.getId(), 0L);

                    double popularityRating = 5.0;
                    if (maxBookings > 0 && count > 0) {
                        // Scale from 5.0 to 10.0
                        popularityRating = 5.0 + ((double) count / maxBookings) * 5.0;
                    }
                    popularityRating = Math.min(10.0, popularityRating);
                    popularityRating = Math.round(popularityRating * 10.0) / 10.0;

                    Set<MovieGenreResponse> genres = movie.getGenres().stream()
                            .map(genre -> new MovieGenreResponse(genre.getId(), genre.getName(), genre.getDescription()))
                            .collect(Collectors.toSet());

                    return new PopularMovieResponse(
                            movie.getId(),
                            movie.getTitle(),
                            movie.getDescription(),
                            movie.getDuration(),
                            movie.getReleaseDate(),
                            movie.getMoviePosterUrl(),
                            movie.getTrailerUrl(),
                            genres,
                            Double.parseDouble(movie.getRating().getDisplayName()),
                            movie.getRating().getDisplayName()
                    );
                })
                .sorted(Comparator.comparing(PopularMovieResponse::popularityRating).reversed())
                .collect(Collectors.toList());
    }

    private MovieRating getRatingFromString(String ratingString) {
        return Arrays.stream(MovieRating.values())
                .filter(r -> r.getDisplayName().equalsIgnoreCase(ratingString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid MovieRating: " + ratingString));
    }

    private MovieLanguage getLanguageFromString(String languageString) {
        return Arrays.stream(MovieLanguage.values())
                .filter(l -> l.getDisplayName().equalsIgnoreCase(languageString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid MovieLanguage: " + languageString));
    }

    private MovieStatus getStatusFromString(String statusString) {
        return Arrays.stream(MovieStatus.values())
                .filter(s -> s.getDisplayName().equalsIgnoreCase(statusString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid MovieStatus: " + statusString));
    }
}
