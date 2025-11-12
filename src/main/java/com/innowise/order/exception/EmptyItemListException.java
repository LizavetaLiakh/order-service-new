package com.innowise.order.exception;

public class EmptyItemListException extends RuntimeException {
    public EmptyItemListException(Iterable<Long> ids) {
        super("No items found with ids: " + ids);
    }
}
