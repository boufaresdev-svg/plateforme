package com.example.BacK.application.g_Stock.Command.marque.updateMarque;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class UpdateMarqueValidator {

    private final IMarqueRepositoryService marqueRepositoryService;

    public UpdateMarqueValidator(IMarqueRepositoryService marqueRepositoryService) {
        this.marqueRepositoryService = marqueRepositoryService;
    }

    public void validate(UpdateMarqueCommand command) {
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la marque est obligatoire");
        }
        
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la marque ne peut pas être vide");
        }
        
        if (command.getNom().length() > 100) {
            throw new IllegalArgumentException("Le nom de la marque ne peut pas dépasser 100 caractères");
        }
    }
}
