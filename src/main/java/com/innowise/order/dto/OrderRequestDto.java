package com.innowise.order.dto;

import com.innowise.order.status.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating an order in REST API requests.
 */
@Data
public class OrderRequestDto {

    /**
     * Unique identifier of the user from table "users". Must be not NULL and must contain at least 1 symbol.
     */
    @NotNull
    private Long userId;

    /**
     * The current status of the order.
     */
    @NotNull
    private Status status;

    /**
     * The date when the order was created.
     */
    @NotNull
    private LocalDate creationDate;
}
