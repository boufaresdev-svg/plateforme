package com.example.BacK.application.g_Stock.Command.category.deleteCategory;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class DeleteCategoryValidator {

    private final ICategoryRepositoryService categoryRepositoryService;

    public DeleteCategoryValidator(ICategoryRepositoryService categoryRepositoryService) {
        this.categoryRepositoryService = categoryRepositoryService;
    }

    public void validate(DeleteCategoryCommand command) {
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la catégorie est obligatoire");
        }
    }
}