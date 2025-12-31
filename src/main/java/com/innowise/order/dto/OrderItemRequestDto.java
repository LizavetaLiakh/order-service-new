package com.innowise.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for creating or updating an order item in REST API requests.
 */
@Data
public class OrderItemRequestDto {

    /**
     * Some {@code Order} from table "orders" that owns current record.
     */
    @NotNull(message = "Order ID must not be NULL")
    private Long orderId;

    /**
     * Some {@code Item} from table "items" to put in the order.
     */
    @NotNull(message = "Item ID must not be NULL")
    private Long itemId;

    /**
     * The amount of mentioned items in the order.
     */
    @NotNull(message = "Quantity must not be NULL")
    private Integer quantity;
}
