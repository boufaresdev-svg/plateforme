package com.example.BacK.application.g_Stock.Command.category.updateCategory;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Category;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateCategoryHandler")
public class UpdateCategoryHandler implements RequestHandler<UpdateCategoryCommand, UpdateCategoryResponse> {

    private final ICategoryRepositoryService categoryRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateCategoryHandler(ICategoryRepositoryService categoryRepositoryService, ModelMapper modelMapper) {
        this.categoryRepositoryService = categoryRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdateCategoryResponse handle(UpdateCategoryCommand command) {
        Category existingCategory = categoryRepositoryService.getById(command.getId());
        if (existingCategory == null) {
            throw new IllegalArgumentException("Catégorie introuvable avec l'ID : " + command.getId());
        }

        // Update fields
        existingCategory.setNom(command.getNom());
        existingCategory.setDescription(command.getDescription());
        if (command.getEstActif() != null) {
            existingCategory.setEstActif(command.getEstActif());
        }

        categoryRepositoryService.update(existingCategory);
        return new UpdateCategoryResponse(command.getId());
    }
}