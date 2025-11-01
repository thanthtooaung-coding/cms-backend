package com.content_management_system.bms.features.showtimes.dto;

import com.content_management_system.bms.features.movies.dto.EnumResponse;
import java.util.List;

public record ShowtimeTemplateResponse(
        List<MovieTemplateData> movies,
        List<TheaterTemplateData> theaters,
        List<EnumResponse> statusOptions
) {}
