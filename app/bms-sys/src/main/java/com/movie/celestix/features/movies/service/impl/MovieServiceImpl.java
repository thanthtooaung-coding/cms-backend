package com.movie.celestix.features.movies.service.impl;

import com.movie.celestix.common.enums.MovieLanguage;
import com.movie.celestix.common.enums.MovieRating;
import com.movie.celestix.common.enums.MovieStatus;
import com.movie.celestix.common.models.Movie;
import com.movie.celestix.common.models.MovieGenre;
import com.movie.celestix.common.models.Showtime;
import com.movie.celestix.common.exception.ResourceNotFoundException;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jpa.BookedSeatJpaRepository;
import com.movie.celestix.common.repository.jpa.MovieGenreJpaRepository;
import com.movie.celestix.common.repository.jpa.MovieJpaRepository;
import com.movie.celestix.common.repository.jpa.ShowtimeJpaRepository;
import com.movie.celestix.features.moviegenres.dto.MovieGenreResponse;
import com.movie.celestix.features.movies.dto.CreateMovieRequest;
import com.movie.celestix.features.movies.dto.MovieResponse;
import com.movie.celestix.features.movies.dto.MovieTemplateResponse;
import com.movie.celestix.features.movies.dto.UpdateMovieRequest;
import com.movie.celestix.features.movies.mapper.MovieMapper;
import com.movie.celestix.features.movies.service.MovieEnumService;
import com.movie.celestix.features.movies.service.MovieService;
import com.movie.celestix.features.publicroutes.dto.PopularMovieResponse;
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
    private final TenantRepository tenantRepository;


    @Override
    @Transactional
    public MovieResponse create(CreateMovieRequest request, Long tenantId) {
        final Movie movie = movieMapper.toEntity(request);
        movie.setRating(getRatingFromString(request.rating()));
        movie.setLanguage(getLanguageFromString(request.language()));
        movie.setStatus(getStatusFromString(request.status()));
        
        // Set tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        movie.setTenant(tenant);
        
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
    public MovieResponse retrieveOne(Long id, Long tenantId) {
        final Movie movie = movieJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie with id " + id + " not found"));
        // Verify tenant matches
        if (movie.getTenant() == null || !movie.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Movie with id " + id + " not found for tenant " + tenantId);
        }
        return movieMapper.toDto(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAll(Long tenantId) {
        return movieJpaRepository.findAllByTenantId(tenantId).stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieResponse update(Long id, UpdateMovieRequest request, Long tenantId) {
        final Movie movie = movieJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie with id " + id + " not found"));
        // Verify tenant matches
        if (movie.getTenant() == null || !movie.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Movie with id " + id + " not found for tenant " + tenantId);
        }
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
    public void delete(Long id, Long tenantId) {
        final Movie movie = movieJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie with id " + id + " not found"));
        // Verify tenant matches
        if (movie.getTenant() == null || !movie.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Movie with id " + id + " not found for tenant " + tenantId);
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
    public List<MovieResponse> retrieveAllByStatus(String status, Long tenantId) {
        MovieStatus movieStatus = getStatusFromString(status);
        return movieJpaRepository.findAllByStatusAndTenantId(movieStatus, tenantId).stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAvailableMovies(Long tenantId) {
        final LocalDate today = LocalDate.now();
        final LocalDate twoDaysFromNow = today.plusDays(2);
        final List<Movie> moviesWithShowtimes = showtimeJpaRepository.findAllByTenantId(tenantId).stream()
                .filter(showtime -> !showtime.getShowtimeDate().isBefore(today) && !showtime.getShowtimeDate().isAfter(twoDaysFromNow))
                .map(Showtime::getMovie)
                .filter(movie -> movie.getTenant() != null && movie.getTenant().getId().equals(tenantId))
                .distinct()
                .toList();

        return moviesWithShowtimes.stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> retrieveAllAvailableMoviesByStatus(String status, Long tenantId) {
        final MovieStatus movieStatus = getStatusFromString(status);
        final List<Movie> moviesByStatus = movieJpaRepository.findAllByStatusAndTenantId(movieStatus, tenantId);
        final LocalDate today = LocalDate.now();
        final LocalDate twoDaysFromNow = today.plusDays(2);

        if (movieStatus == MovieStatus.COMING_SOON) {
            return moviesByStatus.stream()
                    .filter(movie -> movie.getReleaseDate() != null && movie.getReleaseDate().isAfter(twoDaysFromNow))
                    .map(movieMapper::toDto)
                    .collect(Collectors.toList());
        } else { // NOW_SHOWING
            final List<Long> movieIdsWithShowtimes = showtimeJpaRepository.findAllByTenantId(tenantId).stream()
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
    public List<PopularMovieResponse> retrievePopularMovies(Long tenantId) {
        List<Movie> availableMovies = retrieveAvailableMovies(tenantId).stream()
                .map(movieResponse -> movieJpaRepository.findById(movieResponse.id()).orElse(null))
                .filter(Objects::nonNull)
                .filter(movie -> movie.getTenant() != null && movie.getTenant().getId().equals(tenantId))
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
