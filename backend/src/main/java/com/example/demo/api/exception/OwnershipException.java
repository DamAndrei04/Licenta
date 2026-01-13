package com.example.demo.api.exception;

public class OwnershipException extends RuntimeException {
    public OwnershipException() {
        super("Access denied!");
    }
}
