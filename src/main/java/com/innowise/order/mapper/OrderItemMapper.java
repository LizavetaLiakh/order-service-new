package com.innowise.order.mapper;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

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
    OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);

    /**
     * Maps {@code OrderItemRequestDto} to {@code OrderItem} entity.
     * @param orderItemRequestDto DTO object that needs to be mapped
     * @return {@code OrderItem} entity
     */
    OrderItem toOrderItem(OrderItemRequestDto orderItemRequestDto);
}
