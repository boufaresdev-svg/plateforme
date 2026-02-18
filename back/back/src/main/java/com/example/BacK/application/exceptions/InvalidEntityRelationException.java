package com.example.BacK.application.exceptions;

public class InvalidEntityRelationException extends RuntimeException {
    public InvalidEntityRelationException(String message) {
        super(message);
    }
}