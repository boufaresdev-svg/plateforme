package com.example.BacK.application.g_Stock.Command.category.updateCategory;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class UpdateCategoryValidator {

    private final ICategoryRepositoryService categoryRepositoryService;

    public UpdateCategoryValidator(ICategoryRepositoryService categoryRepositoryService) {
        this.categoryRepositoryService = categoryRepositoryService;
    }

    public void validate(UpdateCategoryCommand command) {
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la catégorie est obligatoire");
        }
        
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }
        
        if (command.getNom().length() > 100) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas dépasser 100 caractères");
        }
    }
}