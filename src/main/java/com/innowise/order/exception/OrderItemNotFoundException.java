package com.innowise.order.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(Long id) {
        super("Order item with id " + id + " not found");
    }
}
