package com.example.BacK.application.g_Stock.Command.marque.addMarque;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class AddMarqueValidator {

    private final IMarqueRepositoryService marqueRepositoryService;

    public AddMarqueValidator(IMarqueRepositoryService marqueRepositoryService) {
        this.marqueRepositoryService = marqueRepositoryService;
    }

    public void validate(AddMarqueCommand command) {
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la marque ne peut pas être vide");
        }
        
        if (command.getNom().length() > 100) {
            throw new IllegalArgumentException("Le nom de la marque ne peut pas dépasser 100 caractères");
        }
    }
}
