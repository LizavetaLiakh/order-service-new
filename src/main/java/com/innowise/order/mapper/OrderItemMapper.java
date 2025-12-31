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
            @Mapping(target = "orderId", source = "order.id"),
            @Mapping(target = "itemId", source = "item.id")
    })
    OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);

    /**
     * Maps {@code OrderItemRequestDto} to {@code OrderItem} entity.
     * @param orderItemRequestDto DTO object that needs to be mapped
     * @return {@code OrderItem} entity
     */
    @Mappings({
            @Mapping(target = "order", expression = "java(toOrder(orderItemRequestDto.getOrderId()))"),
            @Mapping(target = "item", expression = "java(toItem(orderItemRequestDto.getItemId()))")
    })
    OrderItem toOrderItem(OrderItemRequestDto orderItemRequestDto);

    default Order toOrder(Long orderId) {
        if (orderId == null) {
            return null;
        }
        Order order = new Order();
        order.setId(orderId);
        return order;
    }

    default Item toItem(Long itemId) {
        if (itemId == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemId);
        return item;
    }
}
