package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieGenreJpaRepository extends JpaRepository<MovieGenre, Long> {
    List<MovieGenre> findAllByTenantId(Long tenantId);
}
