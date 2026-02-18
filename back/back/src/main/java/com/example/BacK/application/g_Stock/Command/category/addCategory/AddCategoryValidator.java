package com.example.BacK.application.g_Stock.Command.category.addCategory;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class AddCategoryValidator {

    private final ICategoryRepositoryService categoryRepositoryService;

    public AddCategoryValidator(ICategoryRepositoryService categoryRepositoryService) {
        this.categoryRepositoryService = categoryRepositoryService;
    }

    public void validate(AddCategoryCommand command) {
        // Add custom validation logic here if needed
        // For example, check if category name already exists
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }
        
        if (command.getNom().length() > 100) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas dépasser 100 caractères");
        }
    }
}