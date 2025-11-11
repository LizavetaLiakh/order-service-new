package com.innowise.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for returning item data in REST API responses.
 */
@Data
public class ItemResponseDto {

    /**
     * Unique identifier of the item.
     */
    private Long id;

    /**
     * The name of the item.
     */
    private String name;

    /**
     * The price of the item.
     */
    private BigDecimal price;
}
