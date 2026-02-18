package com.example.BacK.application.exceptions;

public class FournisseurHasDettesException extends RuntimeException {
    public FournisseurHasDettesException(String message) {
        super(message);
    }
    
    public FournisseurHasDettesException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static FournisseurHasDettesException withId(String id) {
        return new FournisseurHasDettesException(
            "Cannot delete fournisseur with id " + id + " because it has associated dettes. " +
            "Please delete associated dettes first."
        );
    }
}