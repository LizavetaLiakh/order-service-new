package com.innowise.order.mapper;

import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper for converting between {@code Order} entity, {@code OrderRequestDto} and {@code OrderResponseDto}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    /**
     * Maps {@code Order} entity to {@code OrderResponseDto}.
     * @param order entity object that needs to be mapped
     * @return {@code OrderResponseDto} object
     */
    OrderResponseDto toOrderResponseDto(Order order);

    /**
     * Maps {@code OrderRequestDto} to {@code Order} entity.
     * @param orderRequestDto DTO object that needs to be mapped
     * @return {@code Order} entity
     */
    Order toOrder(OrderRequestDto orderRequestDto);
}
