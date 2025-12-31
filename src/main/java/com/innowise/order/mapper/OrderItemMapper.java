package com.innowise.order.mapper;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.entity.Order;
import com.innowise.order.entity.OrderItem;
import com.innowise.order.entity.Item;
import org.mapstruct.*;

/**
 * Mapper for converting between {@code OrderItem} entity, {@code OrderItemRequestDto} and {@code OrderItemResponseDto}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemMapper {

    /**
     * Maps {@code OrderItem} entity to {@code OrderItemResponseDto}.
     * @param orderItem entity object that needs to be mapped
     * @return {@code OrderItemResponseDto} object
     */
    @Mappings({
            @Mapping(target = "orderId", source = "orderId", qualifiedByName = "mapOrderToId"),
            @Mapping(target = "itemId", source = "itemId", qualifiedByName = "mapItemToId")
    })
    OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);

    /**
     * Maps {@code OrderItemRequestDto} to {@code OrderItem} entity.
     * @param orderItemRequestDto DTO object that needs to be mapped
     * @return {@code OrderItem} entity
     */
    @Mappings({
            @Mapping(target = "orderId", source = "orderId", qualifiedByName = "mapToOrder"),
            @Mapping(target = "itemId", source = "itemId", qualifiedByName = "mapToItem")
    })
    OrderItem toOrderItem(OrderItemRequestDto orderItemRequestDto);

    @Named("mapToOrder")
    default Order mapToOrder(Long orderId) {
        if (orderId == null) return null;
        Order order = new Order();
        order.setId(orderId);
        return order;
    }

    @Named("mapToItem")
    default Item mapToItem(Long itemId) {
        if (itemId == null) return null;
        Item item = new Item();
        item.setId(itemId);
        return item;
    }

    @Named("mapOrderToId")
    default Long mapOrderToId(Order order) {
        return order != null ? order.getId() : null;
    }

    @Named("mapItemToId")
    default Long mapItemToId(Item item) {
        return item != null ? item.getId() : null;
    }
}
