package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowtimeJpaRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByShowtimeDateAndShowtimeTimeBetween(LocalDate date, LocalTime start, LocalTime end);
    
    @Query("SELECT s FROM Showtime s WHERE s.movie.tenant.id = :tenantId OR s.theater.tenant.id = :tenantId")
    List<Showtime> findAllByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT s FROM Showtime s WHERE s.showtimeDate = :date AND s.showtimeTime BETWEEN :start AND :end AND (s.movie.tenant.id = :tenantId OR s.theater.tenant.id = :tenantId)")
    List<Showtime> findByShowtimeDateAndShowtimeTimeBetweenAndTenantId(
            @Param("date") LocalDate date, 
            @Param("start") LocalTime start, 
            @Param("end") LocalTime end,
            @Param("tenantId") Long tenantId
    );
    
    boolean existsByMovieId(Long movieId);
    boolean existsByTheaterId(Long theaterId);
}
