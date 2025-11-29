package com.movie.celestix.features.movies.dto;

import java.util.List;

public record MovieTemplateResponse(
        List<EnumResponse> ratingOptions,
        List<EnumResponse> languageOptions,
        List<EnumResponse> statusOptions
) {}
