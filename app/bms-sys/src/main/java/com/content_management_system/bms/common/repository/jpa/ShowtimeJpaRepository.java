package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowtimeJpaRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByShowtimeDateAndShowtimeTimeBetween(LocalDate date, LocalTime start, LocalTime end);
    boolean existsByMovieId(Long movieId);
    boolean existsByTheaterId(Long theaterId);
}
