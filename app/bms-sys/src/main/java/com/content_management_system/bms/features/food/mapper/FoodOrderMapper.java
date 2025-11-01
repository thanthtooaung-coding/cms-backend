package com.content_management_system.bms.features.food.mapper;

import com.content_management_system.bms.common.models.FoodOrder;
import com.content_management_system.bms.common.models.FoodOrderItem;
import com.content_management_system.bms.features.food.dto.FoodOrderHistoryResponse;
import com.content_management_system.bms.features.food.dto.FoodOrderItemDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FoodOrderMapper {

    @Mapping(source = "createdAt", target = "orderDate")
    @Mapping(source = "orderItems", target = "items", qualifiedByName = "mapOrderItems")
    FoodOrderHistoryResponse toHistoryDto(FoodOrder foodOrder);

    List<FoodOrderHistoryResponse> toHistoryDtoList(List<FoodOrder> foodOrders);

    @Named("mapOrderItems")
    default List<FoodOrderItemDetails> mapOrderItems(Set<FoodOrderItem> orderItems) {
        if (orderItems == null) {
            return List.of();
        }
        return orderItems.stream()
                .map(item -> new FoodOrderItemDetails(
                        item.getFood() != null ? item.getFood().getName() : item.getCombo().getComboName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());
    }
}
