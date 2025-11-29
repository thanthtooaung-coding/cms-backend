package com.movie.celestix.features.showtimes.service.impl;

import com.movie.celestix.common.enums.ShowtimeStatus;
import com.movie.celestix.common.models.Configuration;
import com.movie.celestix.common.models.Movie;
import com.movie.celestix.common.models.Showtime;
import com.movie.celestix.common.models.Theater;
import com.movie.celestix.common.repository.jpa.*;
import com.movie.celestix.features.movies.dto.EnumResponse;
import com.movie.celestix.features.showtimes.dto.*;
import com.movie.celestix.features.showtimes.mapper.ShowtimeMapper;
import com.movie.celestix.features.showtimes.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeJpaRepository showtimeJpaRepository;
    private final MovieJpaRepository movieJpaRepository;
    private final TheaterJpaRepository theaterJpaRepository;
    private final ShowtimeMapper showtimeMapper;
    private final ConfigurationJpaRepository configurationJpaRepository;
    private final BookedSeatJpaRepository bookedSeatJpaRepository;

    @Override
    @Transactional
    public ShowtimeResponse create(final CreateShowtimeRequest request, Long tenantId) {
        validateShowtimeSchedulerMinutes(request.showtimeTime());
        final Movie movie = movieJpaRepository.findById(request.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + request.movieId()));
        // Verify movie belongs to tenant
        if (movie.getTenant() == null || !movie.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Movie not found for tenant " + tenantId);
        }
        final Theater theater = theaterJpaRepository.findById(request.theaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found with id: " + request.theaterId()));
        // Verify theater belongs to tenant
        if (theater.getTenant() == null || !theater.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Theater not found for tenant " + tenantId);
        }

        final Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setShowtimeDate(request.showtimeDate());
        showtime.setShowtimeTime(request.showtimeTime());
        showtime.setSeatsAvailable(theater.getCapacity());
        showtime.setStatus(ShowtimeStatus.AVAILABLE);

        final Showtime savedShowtime = showtimeJpaRepository.save(showtime);
        return showtimeMapper.toDto(savedShowtime);
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeResponse retrieveOne(final Long id, Long tenantId) {
        final Showtime showtime = showtimeJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));
        // Verify tenant matches through movie or theater
        if ((showtime.getMovie().getTenant() == null || !showtime.getMovie().getTenant().getId().equals(tenantId)) &&
            (showtime.getTheater().getTenant() == null || !showtime.getTheater().getTenant().getId().equals(tenantId))) {
            throw new RuntimeException("Showtime not found for tenant " + tenantId);
        }
        return showtimeMapper.toDto(showtime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> retrieveAll(Long tenantId) {
        return showtimeJpaRepository.findAllByTenantId(tenantId).stream()
                .map(showtimeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShowtimeResponse update(final Long id, final UpdateShowtimeRequest request, Long tenantId) {
        final Showtime showtime = showtimeJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));
        // Verify tenant matches
        if ((showtime.getMovie().getTenant() == null || !showtime.getMovie().getTenant().getId().equals(tenantId)) &&
            (showtime.getTheater().getTenant() == null || !showtime.getTheater().getTenant().getId().equals(tenantId))) {
            throw new RuntimeException("Showtime not found for tenant " + tenantId);
        }

        if (request.movieId() != null) {
            final Movie movie = movieJpaRepository.findById(request.movieId())
                    .orElseThrow(() -> new RuntimeException("Movie not found with id: " + request.movieId()));
            // Verify movie belongs to tenant
            if (movie.getTenant() == null || !movie.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Movie not found for tenant " + tenantId);
            }
            showtime.setMovie(movie);
        }
        if (request.theaterId() != null) {
            final Theater theater = theaterJpaRepository.findById(request.theaterId())
                    .orElseThrow(() -> new RuntimeException("Theater not found with id: " + request.theaterId()));
            // Verify theater belongs to tenant
            if (theater.getTenant() == null || !theater.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Theater not found for tenant " + tenantId);
            }
            showtime.setTheater(theater);
        }
        if (request.showtimeDate() != null) {
            showtime.setShowtimeDate(request.showtimeDate());
        }
        if (request.showtimeTime() != null) {
            validateShowtimeSchedulerMinutes(request.showtimeTime());
            showtime.setShowtimeTime(request.showtimeTime());
        }
        if (request.status() != null) {
            showtime.setStatus(getStatusFromString(request.status()));
        }
        final Showtime updatedShowtime = showtimeJpaRepository.save(showtime);
        return showtimeMapper.toDto(updatedShowtime);
    }

    @Override
    @Transactional
    public void delete(final Long id, Long tenantId) {
        final Showtime showtime = showtimeJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));
        // Verify tenant matches
        if ((showtime.getMovie().getTenant() == null || !showtime.getMovie().getTenant().getId().equals(tenantId)) &&
            (showtime.getTheater().getTenant() == null || !showtime.getTheater().getTenant().getId().equals(tenantId))) {
            throw new RuntimeException("Showtime not found for tenant " + tenantId);
        }
        if (!bookedSeatJpaRepository.findByShowtimeId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete showtime as there are existing bookings.");
        }
        showtimeJpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeTemplateResponse getShowtimeTemplate(Long tenantId) {
        // Filter movies and theaters by tenant ID
        final List<MovieTemplateData> movies = movieJpaRepository.findAllByTenantId(tenantId).stream()
                .map(movie -> new MovieTemplateData(movie.getId(), movie.getTitle()))
                .collect(Collectors.toList());

        final List<TheaterTemplateData> theaters = theaterJpaRepository.findAllByTenantId(tenantId).stream()
                .map(theater -> new TheaterTemplateData(theater.getId(), theater.getName()))
                .collect(Collectors.toList());

        // Status options are enum values, no tenant filtering needed
        final List<EnumResponse> statuses = Arrays.stream(ShowtimeStatus.values())
                .map(status -> new EnumResponse(status.getId(), status.getDisplayName()))
                .collect(Collectors.toList());

        return new ShowtimeTemplateResponse(movies, theaters, statuses);
    }

    private ShowtimeStatus getStatusFromString(String statusString) {
        return Arrays.stream(ShowtimeStatus.values())
                .filter(s -> s.getDisplayName().equalsIgnoreCase(statusString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid ShowtimeStatus: " + statusString));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupedShowtimeResponse> retrieveAllGroupByMovieAndTheater(final boolean retrieveAll, Long tenantId) {
        final LocalDate today = LocalDate.now();
        final List<Showtime> showtimes = showtimeJpaRepository.findAllByTenantId(tenantId).stream()
                .filter(s -> retrieveAll || s.getShowtimeDate().isEqual(today))
                .filter(s -> (s.getMovie().getTenant() != null && s.getMovie().getTenant().getId().equals(tenantId)) ||
                            (s.getTheater().getTenant() != null && s.getTheater().getTenant().getId().equals(tenantId)))
                .toList();

        return showtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getMovie))
                .entrySet().stream()
                .map(movieEntry -> {
                    Movie movie = movieEntry.getKey();
                    List<TheaterWithShowtimes> theaters = movieEntry.getValue().stream()
                            .collect(Collectors.groupingBy(Showtime::getTheater))
                            .entrySet().stream()
                            .map(theaterEntry -> {
                                Theater theater = theaterEntry.getKey();
                                List<ShowtimeDetail> showtimeDetails = theaterEntry.getValue().stream()
                                        .map(s -> new ShowtimeDetail(
                                                s.getId(),
                                                s.getShowtimeDate(),
                                                s.getShowtimeTime(),
                                                s.getSeatsAvailable(),
                                                s.getStatus().name()
                                        ))
                                        .collect(Collectors.toList());
                                return new TheaterWithShowtimes(
                                        new TheaterInfo(theater.getId(), theater.getName(), theater.getLocation()),
                                        showtimeDetails
                                );
                            })
                            .collect(Collectors.toList());
                    return new GroupedShowtimeResponse(
                            new MovieInfo(movie.getId(), movie.getTitle()),
                            theaters
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupedShowtimeResponse> retrieveByMovieId(final Long movieId, Long tenantId) {
        final Movie movie = movieJpaRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
        // Verify movie belongs to tenant
        if (movie.getTenant() == null || !movie.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Movie not found for tenant " + tenantId);
        }

        final List<Showtime> showtimes = showtimeJpaRepository.findAllByTenantId(tenantId)
                .stream()
                .filter(s -> s.getMovie().getId().equals(movieId))
                .filter(s -> (s.getMovie().getTenant() != null && s.getMovie().getTenant().getId().equals(tenantId)) ||
                            (s.getTheater().getTenant() != null && s.getTheater().getTenant().getId().equals(tenantId)))
                .toList();

        final List<TheaterWithShowtimes> theaters = showtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getTheater))
                .entrySet().stream()
                .map(entry -> {
                    final Theater theater = entry.getKey();
                    final List<ShowtimeDetail> details = entry.getValue().stream()
                            .map(s -> new ShowtimeDetail(
                                    s.getId(),
                                    s.getShowtimeDate(),
                                    s.getShowtimeTime(),
                                    s.getSeatsAvailable(),
                                    s.getStatus().name()
                            ))
                            .collect(Collectors.toList());

                    return new TheaterWithShowtimes(
                            new TheaterInfo(theater.getId(), theater.getName(), theater.getLocation()),
                            details
                    );
                })
                .collect(Collectors.toList());

        return List.of(new GroupedShowtimeResponse(
                new MovieInfo(movie.getId(), movie.getTitle()),
                theaters
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeConflictResponse> findConflictingShowtimes(final int newInterval, Long tenantId) {
        final LocalDate today = LocalDate.now();
        final LocalTime now = LocalTime.now();

        return showtimeJpaRepository.findAllByTenantId(tenantId).stream()
                .filter(st -> st.getShowtimeDate().isAfter(today) || (st.getShowtimeDate().isEqual(today) && st.getShowtimeTime().isAfter(now)))
                .filter(st -> st.getBookedSeats() == null || st.getBookedSeats().isEmpty())
                .filter(st -> st.getShowtimeTime().getMinute() % newInterval != 0)
                .filter(st -> (st.getMovie().getTenant() != null && st.getMovie().getTenant().getId().equals(tenantId)) ||
                              (st.getTheater().getTenant() != null && st.getTheater().getTenant().getId().equals(tenantId)))
                .map(st -> new ShowtimeConflictResponse(
                        st.getMovie().getTitle(),
                        st.getTheater().getName(),
                        st.getShowtimeDate(),
                        st.getShowtimeTime()
                ))
                .collect(Collectors.toList());
    }

    private void validateShowtimeSchedulerMinutes(final LocalTime showtimeTime) {
        final Configuration config = configurationJpaRepository.findByCode("SHOWTIME_SCHEDULER_MINUTES")
                .orElseGet(() -> {
                    Configuration newConfig = new Configuration();
                    newConfig.setCode("SHOWTIME_SCHEDULER_MINUTES");
                    newConfig.setValue("10");
                    return newConfig;
                });
        final int minuteIncrements = Integer.parseInt(config.getValue());

        if (showtimeTime.getMinute() % minuteIncrements != 0) {
            throw new IllegalArgumentException("Showtime must be in " + minuteIncrements + "-minute increments.");
        }
    }
}
