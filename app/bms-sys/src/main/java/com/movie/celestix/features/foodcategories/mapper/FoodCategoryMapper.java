package com.movie.celestix.features.foodcategories.mapper;

import com.movie.celestix.common.models.FoodCategory;
import com.movie.celestix.features.foodcategories.dto.FoodCategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodCategoryMapper {
    FoodCategoryResponse toDto(FoodCategory foodCategory);
    List<FoodCategoryResponse> toDtoList(List<FoodCategory> categories);
}
