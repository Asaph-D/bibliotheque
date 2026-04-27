package com.bibliotheque.rest.api;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

