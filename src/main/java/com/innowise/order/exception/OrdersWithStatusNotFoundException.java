package com.innowise.order.exception;

public class OrdersWithStatusNotFoundException extends RuntimeException {
    public OrdersWithStatusNotFoundException(String statusName) {
        super("Orders with status " + statusName + " not found");
    }
}
