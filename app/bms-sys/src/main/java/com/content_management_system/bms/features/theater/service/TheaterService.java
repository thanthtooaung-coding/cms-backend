package com.content_management_system.bms.features.theater.service;

import com.content_management_system.bms.features.theater.dto.CreateTheaterRequest;
import com.content_management_system.bms.features.theater.dto.TheaterResponse;
import com.content_management_system.bms.features.theater.dto.UpdateTheaterRequest;

import java.util.List;

public interface TheaterService {
    TheaterResponse create(CreateTheaterRequest request);
    TheaterResponse retrieveOne(Long id);
    List<TheaterResponse> retrieveAll();
    TheaterResponse update(Long id, UpdateTheaterRequest request);
    void delete(Long id);
}
