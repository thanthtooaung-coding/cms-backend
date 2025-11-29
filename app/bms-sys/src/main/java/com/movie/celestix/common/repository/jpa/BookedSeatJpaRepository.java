package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedSeatJpaRepository extends JpaRepository<BookedSeat, Long> {
    List<BookedSeat> findByShowtimeId(Long showtimeId);

    @Query("SELECT s.movie.id as movieId, COUNT(bs.id) as bookingCount " +
            "FROM BookedSeat bs JOIN bs.showtime s " +
            "GROUP BY s.movie.id")
    List<MovieBookingCount> countBookingsByMovie();

    interface MovieBookingCount {
        Long getMovieId();
        Long getBookingCount();
    }
}
