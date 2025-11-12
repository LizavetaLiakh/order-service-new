package com.innowise.order.exception;

public class EmptyEntityListException extends RuntimeException {
    public EmptyEntityListException(String entityListName, Iterable<Long> ids) {
        super("No " + entityListName + " found with ids: " + ids);
    }
}
