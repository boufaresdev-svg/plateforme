package com.example.BacK.application.g_Fournisseur.Command.fournisseur.addFournisseur;

import org.springframework.stereotype.Component;

@Component("AddFournisseurValidator")
public class AddFournisseurValidator {
    
    public boolean validate(AddFournisseurCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command ne peut pas être null");
        }
        
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du fournisseur est obligatoire");
        }
        
        if (command.getTelephone() != null && !command.getTelephone().trim().isEmpty()) {
            
            if (!command.getTelephone().matches("^[+]?[0-9\\s-()]+$")) {
                throw new IllegalArgumentException("Format de téléphone invalide");
            }
        }
        
        return true;
    }
}