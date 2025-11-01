package com.content_management_system.bms.features.showtimes.service;

import com.content_management_system.bms.features.showtimes.dto.*;

import java.util.List;

public interface ShowtimeService {
    ShowtimeResponse create(CreateShowtimeRequest request);
    ShowtimeResponse retrieveOne(Long id);
    List<ShowtimeResponse> retrieveAll();
    ShowtimeResponse update(Long id, UpdateShowtimeRequest request);
    void delete(Long id);
    ShowtimeTemplateResponse getShowtimeTemplate();
    List<GroupedShowtimeResponse> retrieveAllGroupByMovieAndTheater(boolean retrieveAll);
    List<GroupedShowtimeResponse> retrieveByMovieId(Long movieId);
    List<ShowtimeConflictResponse> findConflictingShowtimes(int newInterval);
}
