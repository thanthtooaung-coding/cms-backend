package com.movie.celestix.features.movies.service.impl;

import com.movie.celestix.common.enums.MovieLanguage;
import com.movie.celestix.common.enums.MovieRating;
import com.movie.celestix.common.enums.MovieStatus;
import com.movie.celestix.features.movies.dto.EnumResponse;
import com.movie.celestix.features.movies.service.MovieEnumService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieEnumServiceImpl implements MovieEnumService {

    @Override
    public List<EnumResponse> getAllRatings() {
        return Arrays.stream(MovieRating.values())
                .map(rating -> new EnumResponse(rating.getId(), rating.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnumResponse> getAllLanguages() {
        return Arrays.stream(MovieLanguage.values())
                .map(language -> new EnumResponse(language.getId(), language.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnumResponse> getAllStatuses() {
        return Arrays.stream(MovieStatus.values())
                .map(status -> new EnumResponse(status.getId(), status.getDisplayName()))
                .collect(Collectors.toList());
    }
}
