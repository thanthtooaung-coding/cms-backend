package com.movie.celestix.features.movies.service;

import com.movie.celestix.features.movies.dto.EnumResponse;

import java.util.List;

public interface MovieEnumService {
    List<EnumResponse> getAllRatings();
    List<EnumResponse> getAllLanguages();
    List<EnumResponse> getAllStatuses();
}
