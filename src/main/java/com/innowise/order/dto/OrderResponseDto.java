package com.innowise.order.dto;

import com.innowise.order.client.UserResponseDto;
import com.innowise.order.status.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for returning order data in REST API responses.
 */
@Data
public class OrderResponseDto {

    /**
     * Unique identifier of the order.
     */
    private Long id;

    /**
     * Unique identifier of the {@code User} from table "users".
     */
    private Long userId;

    /**
     * The current status of the order.
     */
    private OrderStatus orderStatus;

    /**
     * The date when the order was created.
     */
    private LocalDate creationDate;

    /**
     * User with {@code userId} who own current order.
     */
    private UserResponseDto user;
}
