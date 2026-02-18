package com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateCategorieHandler")
public class UpdateCategorieHandler implements RequestHandler<UpdateCategorieCommand, Void> {

    private final CategorieRepositoryService categorieRepositoryService;
    private final TypeRepositoryService typeRepositoryService;

    public UpdateCategorieHandler(CategorieRepositoryService categorieRepositoryService,
                                  TypeRepositoryService typeRepositoryService) {
        this.categorieRepositoryService = categorieRepositoryService;
        this.typeRepositoryService = typeRepositoryService;
    }

    @Override
    public Void handle(UpdateCategorieCommand command) {
        Categorie categorieExistante = categorieRepositoryService
                .getCategorieById(command.getIdCategorie())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID : " + command.getIdCategorie()));

        if (command.getNom() != null && !command.getNom().isBlank()) {
            categorieExistante.setNom(command.getNom());
        }
        if (command.getDescription() != null) {
            categorieExistante.setDescription(command.getDescription());
        }

        if (command.getIdType() != null) {
            Type type = typeRepositoryService.getTypeById(command.getIdType())
                    .orElseThrow(() -> new IllegalArgumentException("Type non trouvé avec l'ID : " + command.getIdType()));
            categorieExistante.setType(type);
        }

        categorieRepositoryService.saveCategorie(categorieExistante);

        return null;
    }
}


