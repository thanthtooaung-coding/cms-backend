package com.movie.celestix.common.repository.jdbc;

public interface UserJdbcRepository {
    String findPasswordByEmail(String email);
    boolean existsByEmail(String email);
}
