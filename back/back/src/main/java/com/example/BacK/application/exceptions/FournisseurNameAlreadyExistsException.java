package com.example.BacK.application.exceptions;

public class FournisseurNameAlreadyExistsException extends RuntimeException {
    
    public FournisseurNameAlreadyExistsException(String name) {
        super("Un fournisseur avec le nom '" + name + "' existe déjà");
    }
    
    public static FournisseurNameAlreadyExistsException withName(String name) {
        return new FournisseurNameAlreadyExistsException(name);
    }
}