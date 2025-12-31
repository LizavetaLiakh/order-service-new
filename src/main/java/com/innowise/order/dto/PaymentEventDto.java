package com.innowise.order.dto;

import com.innowise.order.status.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentEventDto {
    private Long id;
    private Long orderId;
    private Long userId;
    private PaymentStatus status;
    private LocalDate creationDate;
    private String source;
}