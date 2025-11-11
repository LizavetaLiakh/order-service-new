package com.innowise.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creating or updating an item in REST API requests.
 */
@Data
public class ItemRequestDto {

    /**
     * The name of the item.
     */
    @NotBlank
    private String name;

    /**
     * The price of the item. Must be a real number with 2 decimal places.
     */
    @NotNull
    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}
