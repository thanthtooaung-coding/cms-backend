package com.movie.celestix.common.repository.jdbc.impl;

import com.movie.celestix.common.repository.jdbc.UserJdbcRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcRepositoryImpl implements UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcRepositoryImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String findPasswordByEmail(String email) {
        String sql = "SELECT password FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, email);
        } catch (EmptyResultDataAccessException e) {
            return null; // no user with that email
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        final String sql = "SELECT COUNT(1) FROM users WHERE email = ?";
        final Integer count = this.jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }
}
