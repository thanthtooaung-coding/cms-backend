package com.movie.celestix.common.repository.jdbc;

import com.movie.celestix.features.theater.dto.TheaterResponse;

import java.util.List;

public interface TheaterJdbcRepository {
    TheaterResponse findById(Long id);
    List<TheaterResponse> findAll();
}
