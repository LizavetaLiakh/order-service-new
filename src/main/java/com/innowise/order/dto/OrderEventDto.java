package com.innowise.order.dto;

import com.innowise.order.status.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderEventDto {
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private LocalDate creationDate;
    private String source;
}
