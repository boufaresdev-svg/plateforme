package com.example.BacK.application.g_Fournisseur.Command.fournisseur.deleteFournisseur;

import org.springframework.stereotype.Component;

@Component("DeleteFournisseurValidator")
public class DeleteFournisseurValidator {
    
    public boolean validate(DeleteFournisseurCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command ne peut pas être null");
        }
        
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du fournisseur est obligatoire");
        }
        
        return true;
    }
}