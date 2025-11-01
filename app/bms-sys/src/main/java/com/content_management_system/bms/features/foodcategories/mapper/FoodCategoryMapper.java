package com.content_management_system.bms.features.foodcategories.mapper;

import com.content_management_system.bms.common.models.FoodCategory;
import com.content_management_system.bms.features.foodcategories.dto.FoodCategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodCategoryMapper {
    FoodCategoryResponse toDto(FoodCategory foodCategory);
    List<FoodCategoryResponse> toDtoList(List<FoodCategory> categories);
}
