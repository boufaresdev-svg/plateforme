package com.example.BacK.application.g_Formation.Command.SousCategorie.UpdateSousCategorie;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.SousCategorie;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.SousCategorieRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateSousCategorieHandler")
public class UpdateSousCategorieHandler implements RequestHandler<UpdateSousCategorieCommand, Void> {

    private final SousCategorieRepositoryService sousCategorieRepositoryService;
    private final CategorieRepositoryService categorieRepositoryService;

    public UpdateSousCategorieHandler(SousCategorieRepositoryService sousCategorieRepositoryService,
                                      CategorieRepositoryService categorieRepositoryService) {
        this.sousCategorieRepositoryService = sousCategorieRepositoryService;
        this.categorieRepositoryService = categorieRepositoryService;
    }

    @Override
    public Void handle(UpdateSousCategorieCommand command) {

        if (command.getIdSousCategorie() == null) {
            throw new IllegalArgumentException("L'ID de la sous-catégorie est obligatoire pour la mise à jour.");
        }

        if (!sousCategorieRepositoryService.existsById(command.getIdSousCategorie())) {
            throw new IllegalArgumentException("Sous-catégorie introuvable avec l'ID : " + command.getIdSousCategorie());
        }

        SousCategorie existing = sousCategorieRepositoryService
                .getSousCategorieById(command.getIdSousCategorie())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sous-catégorie non trouvée avec l'ID : " + command.getIdSousCategorie()
                ));

        existing.setNom(command.getNom());
        existing.setDescription(command.getDescription());

        if (command.getIdCategorie() != null) {
            Categorie categorie = categorieRepositoryService
                    .getCategorieById(command.getIdCategorie())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Catégorie introuvable avec l'ID : " + command.getIdCategorie()
                    ));
            existing.setCategorie(categorie);
        }

        sousCategorieRepositoryService.updateSousCategorie(command.getIdSousCategorie(), existing);

        return null;
    }
}
