package com.movie.celestix.features.theater.service;

import com.movie.celestix.features.theater.dto.CreateTheaterRequest;
import com.movie.celestix.features.theater.dto.TheaterResponse;
import com.movie.celestix.features.theater.dto.UpdateTheaterRequest;

import java.util.List;

public interface TheaterService {
    TheaterResponse create(CreateTheaterRequest request, Long tenantId);
    TheaterResponse retrieveOne(Long id, Long tenantId);
    List<TheaterResponse> retrieveAll(Long tenantId);
    TheaterResponse update(Long id, UpdateTheaterRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}
