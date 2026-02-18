package com.example.BacK.application.g_Stock.Command.category.deleteCategory;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteCategoryHandler")
public class DeleteCategoryHandler implements RequestHandler<DeleteCategoryCommand, DeleteCategoryResponse> {

    private final ICategoryRepositoryService categoryRepositoryService;

    public DeleteCategoryHandler(ICategoryRepositoryService categoryRepositoryService) {
        this.categoryRepositoryService = categoryRepositoryService;
    }

    @Override
    public DeleteCategoryResponse handle(DeleteCategoryCommand command) {
        // Check if category exists before deleting
        if (categoryRepositoryService.getById(command.getId()) == null) {
            throw new IllegalArgumentException("Catégorie introuvable avec l'ID : " + command.getId());
        }

        categoryRepositoryService.delete(command.getId());
        return new DeleteCategoryResponse(command.getId());
    }
}