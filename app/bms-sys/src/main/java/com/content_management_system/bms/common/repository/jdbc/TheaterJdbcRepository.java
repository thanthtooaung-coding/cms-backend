package com.content_management_system.bms.common.repository.jdbc;

import com.content_management_system.bms.features.theater.dto.TheaterResponse;

import java.util.List;

public interface TheaterJdbcRepository {
    TheaterResponse findById(Long id);
    List<TheaterResponse> findAll();
}
