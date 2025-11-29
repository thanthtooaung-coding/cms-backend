package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.enums.MovieStatus;
import com.movie.celestix.common.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieJpaRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAllByStatus(MovieStatus status);
    List<Movie> findAllByTenantId(Long tenantId);
    List<Movie> findAllByStatusAndTenantId(MovieStatus status, Long tenantId);
    boolean existsByGenres_Id(Long id);
    long countByTenantId(Long tenantId);
}
