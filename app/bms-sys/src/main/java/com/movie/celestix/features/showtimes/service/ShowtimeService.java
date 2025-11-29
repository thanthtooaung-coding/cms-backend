package com.movie.celestix.features.showtimes.service;

import com.movie.celestix.features.showtimes.dto.*;

import java.util.List;

public interface ShowtimeService {
    ShowtimeResponse create(CreateShowtimeRequest request, Long tenantId);
    ShowtimeResponse retrieveOne(Long id, Long tenantId);
    List<ShowtimeResponse> retrieveAll(Long tenantId);
    ShowtimeResponse update(Long id, UpdateShowtimeRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
    ShowtimeTemplateResponse getShowtimeTemplate(Long tenantId);
    List<GroupedShowtimeResponse> retrieveAllGroupByMovieAndTheater(boolean retrieveAll, Long tenantId);
    List<GroupedShowtimeResponse> retrieveByMovieId(Long movieId, Long tenantId);
    List<ShowtimeConflictResponse> findConflictingShowtimes(int newInterval, Long tenantId);
}
