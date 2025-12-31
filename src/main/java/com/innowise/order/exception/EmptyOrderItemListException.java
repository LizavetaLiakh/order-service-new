package com.innowise.order.exception;

public class EmptyOrderItemListException extends RuntimeException {
    public EmptyOrderItemListException(Iterable<Long> ids) {
        super("No order items found with ids: " + ids);
    }
}
