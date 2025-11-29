package com.movie.celestix.features.theater.mapper;

import com.movie.celestix.common.models.SeatConfiguration;
import com.movie.celestix.common.models.SeatInfo;
import com.movie.celestix.common.models.Theater;
import com.movie.celestix.features.theater.dto.CreateTheaterRequest;
import com.movie.celestix.features.theater.dto.SeatConfigurationData;
import com.movie.celestix.features.theater.dto.SeatInfoData;
import com.movie.celestix.features.theater.dto.TheaterResponse;
import com.movie.celestix.features.theater.dto.UpdateTheaterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TheaterMapper {

    Theater toEntity(CreateTheaterRequest request);

    TheaterResponse toDto(Theater theater);

    void updateTheaterFromDto(UpdateTheaterRequest request, @MappingTarget Theater theater);

    default SeatConfiguration toEntity(SeatConfigurationData data) {
        if (data == null) {
            return null;
        }
        final SeatConfiguration config = new SeatConfiguration();
        config.setRow(data.row());
        config.setColumn(data.column());
        return config;
    }

    default SeatInfo toEntity(SeatInfoData data) {
        if (data == null) {
            return null;
        }
        final SeatInfo info = new SeatInfo();
        info.setTotalRows(data.totalRows());
        info.setTotalPrice(data.totalPrice());
        return info;
    }
}
