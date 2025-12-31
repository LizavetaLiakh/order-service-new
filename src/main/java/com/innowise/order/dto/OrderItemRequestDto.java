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
    @NotNull
    private Long orderId;

    /**
     * Some {@code Item} from table "items" to put in the order.
     */
    @NotNull
    private Long itemId;

    /**
     * The amount of mentioned items in the order.
     */
    @NotNull
    private Integer quantity;
}
