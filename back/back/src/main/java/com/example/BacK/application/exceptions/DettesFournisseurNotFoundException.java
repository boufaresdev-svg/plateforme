package com.example.BacK.application.exceptions;

public class DettesFournisseurNotFoundException extends RuntimeException {
    public DettesFournisseurNotFoundException(String message) {
        super(message);
    }
    
    public DettesFournisseurNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DettesFournisseurNotFoundException withId(String id) {
        return new DettesFournisseurNotFoundException("DettesFournisseur not found with id: " + id);
    }
}