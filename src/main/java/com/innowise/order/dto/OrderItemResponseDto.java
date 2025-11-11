package com.innowise.order.dto;

import lombok.Data;

/**
 * Data Transfer Object for returning order item data in REST API responses.
 */
@Data
public class OrderItemResponseDto {

    /**
     * Unique identifier of the order item.
     */
    private Long id;

    /**
     * The order that owns current record.
     */
    private Long orderId;

    /**
     * The item inside the order.
     */
    private Long itemId;

    /**
     * The amount of mentioned items in the order.
     */
    private Integer quantity;
}
