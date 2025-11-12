package com.innowise.order.exception;

public class EmptyOrderListException extends RuntimeException {
    public EmptyOrderListException(Iterable<Long> ids) {
        super("No orders found with ids: " + ids);
    }
}
