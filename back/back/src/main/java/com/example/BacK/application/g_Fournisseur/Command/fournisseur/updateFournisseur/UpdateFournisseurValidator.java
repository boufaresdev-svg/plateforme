package com.example.BacK.application.g_Fournisseur.Command.fournisseur.updateFournisseur;

import org.springframework.stereotype.Component;

@Component("UpdateFournisseurValidator")
public class UpdateFournisseurValidator {
    
    public boolean validate(UpdateFournisseurCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command ne peut pas être null");
        }
        
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du fournisseur est obligatoire");
        }
        
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du fournisseur est obligatoire");
        }
        
        return true;
    }
}