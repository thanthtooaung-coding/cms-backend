package com.content_management_system.bms.features.showtimes.service.impl;

import com.content_management_system.bms.common.enums.ShowtimeStatus;
import com.content_management_system.bms.common.models.Configuration;
import com.content_management_system.bms.common.models.Movie;
import com.content_management_system.bms.common.models.Showtime;
import com.content_management_system.bms.common.models.Theater;
import com.content_management_system.bms.common.repository.jpa.*;
import com.content_management_system.bms.features.showtimes.dto.*;
import com.content_management_system.bms.features.movies.dto.EnumResponse;
import com.content_management_system.bms.features.showtimes.mapper.ShowtimeMapper;
import com.content_management_system.bms.features.showtimes.service.ShowtimeService;
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
    public ShowtimeResponse create(final CreateShowtimeRequest request) {
        validateShowtimeSchedulerMinutes(request.showtimeTime());
        final Movie movie = movieJpaRepository.findById(request.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + request.movieId()));
        final Theater theater = theaterJpaRepository.findById(request.theaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found with id: " + request.theaterId()));

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
    public ShowtimeResponse retrieveOne(final Long id) {
        final Showtime showtime = showtimeJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));
        return showtimeMapper.toDto(showtime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> retrieveAll() {
        return showtimeJpaRepository.findAll().stream()
                .map(showtimeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShowtimeResponse update(final Long id, final UpdateShowtimeRequest request) {
        final Showtime showtime = showtimeJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));

        if (request.movieId() != null) {
            final Movie movie = movieJpaRepository.findById(request.movieId())
                    .orElseThrow(() -> new RuntimeException("Movie not found with id: " + request.movieId()));
            showtime.setMovie(movie);
        }
        if (request.theaterId() != null) {
            final Theater theater = theaterJpaRepository.findById(request.theaterId())
                    .orElseThrow(() -> new RuntimeException("Theater not found with id: " + request.theaterId()));
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
    public void delete(final Long id) {
        if (!showtimeJpaRepository.existsById(id)) {
            throw new RuntimeException("Showtime not found with id: " + id);
        }
        if (!bookedSeatJpaRepository.findByShowtimeId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete showtime as there are existing bookings.");
        }
        showtimeJpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeTemplateResponse getShowtimeTemplate() {
        final List<MovieTemplateData> movies = movieJpaRepository.findAll().stream()
                .map(movie -> new MovieTemplateData(movie.getId(), movie.getTitle()))
                .collect(Collectors.toList());

        final List<TheaterTemplateData> theaters = theaterJpaRepository.findAll().stream()
                .map(theater -> new TheaterTemplateData(theater.getId(), theater.getName()))
                .collect(Collectors.toList());

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
    public List<GroupedShowtimeResponse> retrieveAllGroupByMovieAndTheater(final boolean retrieveAll) {
        final LocalDate today = LocalDate.now();
        final List<Showtime> showtimes = showtimeJpaRepository.findAll().stream()
                .filter(s -> retrieveAll || s.getShowtimeDate().isEqual(today))
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
    public List<GroupedShowtimeResponse> retrieveByMovieId(final Long movieId) {
        final Movie movie = movieJpaRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        final List<Showtime> showtimes = showtimeJpaRepository.findAll()
                .stream()
                .filter(s -> s.getMovie().getId().equals(movieId))
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
    public List<ShowtimeConflictResponse> findConflictingShowtimes(final int newInterval) {
        final LocalDate today = LocalDate.now();
        final LocalTime now = LocalTime.now();

        return showtimeJpaRepository.findAll().stream()
                .filter(st -> st.getShowtimeDate().isAfter(today) || (st.getShowtimeDate().isEqual(today) && st.getShowtimeTime().isAfter(now)))
                .filter(st -> st.getBookedSeats() == null || st.getBookedSeats().isEmpty())
                .filter(st -> st.getShowtimeTime().getMinute() % newInterval != 0)
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
                .orElse(new Configuration("SHOWTIME_SCHEDULER_MINUTES", "10"));
        final int minuteIncrements = Integer.parseInt(config.getValue());

        if (showtimeTime.getMinute() % minuteIncrements != 0) {
            throw new IllegalArgumentException("Showtime must be in " + minuteIncrements + "-minute increments.");
        }
    }
}
