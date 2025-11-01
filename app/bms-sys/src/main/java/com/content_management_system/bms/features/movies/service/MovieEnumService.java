package com.content_management_system.bms.features.movies.service;

import com.content_management_system.bms.features.movies.dto.EnumResponse;

import java.util.List;

public interface MovieEnumService {
    List<EnumResponse> getAllRatings();
    List<EnumResponse> getAllLanguages();
    List<EnumResponse> getAllStatuses();
}
