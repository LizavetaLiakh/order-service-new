package com.innowise.order.exception;

public class EmptyOrderItemListSingleIdException extends RuntimeException {
    public EmptyOrderItemListSingleIdException(String idType, Long id) {
        super("No order items found with " + idType + " id " + id);
    }
}
