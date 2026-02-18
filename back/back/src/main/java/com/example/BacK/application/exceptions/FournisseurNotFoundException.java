package com.example.BacK.application.exceptions;

public class FournisseurNotFoundException extends RuntimeException {
    public FournisseurNotFoundException(String message) {
        super(message);
    }
    
    public FournisseurNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static FournisseurNotFoundException withId(String id) {
        return new FournisseurNotFoundException("Fournisseur not found with id: " + id);
    }
}