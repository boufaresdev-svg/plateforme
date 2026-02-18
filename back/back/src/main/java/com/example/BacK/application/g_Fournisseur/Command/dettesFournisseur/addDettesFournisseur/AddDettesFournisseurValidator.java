package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.addDettesFournisseur;

import org.springframework.stereotype.Component;

@Component("AddDettesFournisseurValidator")
public class AddDettesFournisseurValidator {
    
    public boolean validate(AddDettesFournisseurCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command ne peut pas être null");
        }
        
        if (command.getMontantTotal() == null || command.getMontantTotal() <= 0) {
            throw new IllegalArgumentException("Le montant total doit être positif");
        }
        
        if (command.getFournisseurId() == null || command.getFournisseurId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du fournisseur est obligatoire");
        }
        
        if (command.getDatePaiementPrevue() == null) {
            throw new IllegalArgumentException("La date de paiement prévue est obligatoire");
        }
        
        return true;
    }
}