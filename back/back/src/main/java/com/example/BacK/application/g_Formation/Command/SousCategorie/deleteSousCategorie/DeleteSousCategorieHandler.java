package com.example.BacK.application.g_Formation.Command.SousCategorie.deleteSousCategorie;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.SousCategorieRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteSousCategorieHandler")
public class DeleteSousCategorieHandler implements RequestHandler<DeleteSousCategorieCommand, Void> {

    private final SousCategorieRepositoryService sousCategorieRepositoryService;

    public DeleteSousCategorieHandler(SousCategorieRepositoryService sousCategorieRepositoryService) {
        this.sousCategorieRepositoryService = sousCategorieRepositoryService;
    }

    @Override
    public Void handle(DeleteSousCategorieCommand command) {
        if (command.getIdSousCategorie() == null) {
            throw new IllegalArgumentException("L'ID de la sous-catégorie ne peut pas être nul.");
        }

        boolean exists = sousCategorieRepositoryService.existsById(command.getIdSousCategorie());
        if (!exists) {
            throw new IllegalArgumentException(
                    "Sous-catégorie introuvable avec l'ID : " + command.getIdSousCategorie()
            );
        }

        sousCategorieRepositoryService.deleteSousCategorie(command.getIdSousCategorie());

        return null;
    }
}
