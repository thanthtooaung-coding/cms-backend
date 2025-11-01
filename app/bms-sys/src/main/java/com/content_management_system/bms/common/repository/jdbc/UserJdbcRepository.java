package com.content_management_system.bms.common.repository.jdbc;

public interface UserJdbcRepository {
    String findPasswordByEmail(String email);
    boolean existsByEmail(String email);
}
