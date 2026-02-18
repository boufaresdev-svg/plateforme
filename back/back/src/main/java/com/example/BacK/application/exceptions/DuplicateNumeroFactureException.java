package com.example.BacK.application.exceptions;

public class DuplicateNumeroFactureException extends RuntimeException {
    public DuplicateNumeroFactureException(String message) {
        super(message);
    }
    
    public DuplicateNumeroFactureException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DuplicateNumeroFactureException withNumeroFacture(String numeroFacture) {
        return new DuplicateNumeroFactureException("Une dette avec le numéro de facture '" + numeroFacture + "' existe déjà");
    }
}
