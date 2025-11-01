package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.enums.MovieStatus;
import com.content_management_system.bms.common.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieJpaRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAllByStatus(MovieStatus status);
    boolean existsByGenres_Id(Long id);
}
