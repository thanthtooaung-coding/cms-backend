package com.movie.celestix.common.repository.jdbc.impl;

import com.movie.celestix.common.repository.jdbc.TheaterJdbcRepository;
import com.movie.celestix.features.theater.dto.TheaterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TheaterJdbcRepositoryImpl implements TheaterJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_BY_ID_QUERY = """
        SELECT
            t.id,
            t.name,
            t.location,
            t.seat_row,
            t.seat_column,
            t.total_premium_rows,
            t.total_premium_price,
            t.total_regular_rows,
            t.total_regular_price,
            t.total_economy_rows,
            t.total_economy_price,
            t.total_basic_rows,
            t.total_basic_price
        FROM theaters t
        WHERE t.id = ?
          AND t.deleted_at IS NULL
    """;

    private static final String FIND_ALL_QUERY = """
        SELECT
            t.id,
            t.name,
            t.location,
            t.seat_row,
            t.seat_column,
            t.total_premium_rows,
            t.total_premium_price,
            t.total_regular_rows,
            t.total_regular_price,
            t.total_economy_rows,
            t.total_economy_price,
            t.total_basic_rows,
            t.total_basic_price
        FROM theaters t
        WHERE t.deleted_at IS NULL
    """;

    @Override
    public TheaterResponse findById(final Long id) {
        return this.jdbcTemplate.queryForObject(
                FIND_BY_ID_QUERY,
                TheaterResponse.MAPPER,
                id
        );
    }

    @Override
    public List<TheaterResponse> findAll() {
        return this.jdbcTemplate.query(
                FIND_ALL_QUERY,
                TheaterResponse.MAPPER
        );
    }
}
