package com.innowise.order.exception;

public class OrdersWithUserIdNotFoundException extends RuntimeException {
    public OrdersWithUserIdNotFoundException(Long userId) {
        super("Orders with user ID " + userId + " not found");
    }
}
