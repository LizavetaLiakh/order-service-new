package com.innowise.order.mapper;

import com.innowise.order.dto.ItemRequestDto;
import com.innowise.order.dto.ItemResponseDto;
import com.innowise.order.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper for converting between {@code Item} entity, {@code ItemRequestDto} and {@code ItemResponseDto}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    /**
     * Maps {@code Item} entity to {@code ItemResponseDto}.
     * @param item entity object that needs to be mapped
     * @return {@code ItemResponseDto} object
     */
    ItemResponseDto toItemResponseDto(Item item);

    /**
     * Maps {@code ItemRequestDto} to {@code Item} entity.
     * @param itemRequestDto DTO object that needs to be mapped
     * @return {@code Item} entity
     */
    Item toItem(ItemRequestDto itemRequestDto);
}
