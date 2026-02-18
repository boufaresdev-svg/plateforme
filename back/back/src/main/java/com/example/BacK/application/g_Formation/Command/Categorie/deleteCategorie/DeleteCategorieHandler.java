package com.example.BacK.application.g_Formation.Command.Categorie.deleteCategorie;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteCategorieHandler")
public class DeleteCategorieHandler implements RequestHandler<DeleteCategorieCommand, Void> {

    private final CategorieRepositoryService categorieRepositoryService;

    public DeleteCategorieHandler(CategorieRepositoryService categorieRepositoryService) {
        this.categorieRepositoryService = categorieRepositoryService;
    }

    @Override
    public Void handle(DeleteCategorieCommand command) {
        boolean exists = categorieRepositoryService.existsById(command.getIdCategorie());
        if (!exists) {
            throw new IllegalArgumentException("La catégorie avec l'ID " + command.getIdCategorie() + " n'existe pas.");
        }

        categorieRepositoryService.deleteCategorie(command.getIdCategorie());

        return null;
    }
}

