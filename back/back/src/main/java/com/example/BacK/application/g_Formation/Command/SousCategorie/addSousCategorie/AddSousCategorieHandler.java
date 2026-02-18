package com.example.BacK.application.g_Formation.Command.SousCategorie.addSousCategorie;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.SousCategorie;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.SousCategorieRepositoryService;
import org.springframework.stereotype.Component;

@Component("AddSousCategorieHandler")
public class AddSousCategorieHandler implements RequestHandler<AddSousCategorieCommand, AddSousCategorieResponse> {

    private final SousCategorieRepositoryService sousCategorieRepositoryService;
    private final CategorieRepositoryService categorieRepositoryService;

    public AddSousCategorieHandler(SousCategorieRepositoryService sousCategorieRepositoryService,
                                   CategorieRepositoryService categorieRepositoryService) {
        this.sousCategorieRepositoryService = sousCategorieRepositoryService;
        this.categorieRepositoryService = categorieRepositoryService;
    }

    @Override
    public AddSousCategorieResponse handle(AddSousCategorieCommand command) {
        if (command.getNom() == null || command.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la sous-catégorie ne peut pas être vide.");
        }

        Categorie categorie = categorieRepositoryService.getCategorieById(command.getIdCategorie())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Catégorie introuvable avec l'ID : " + command.getIdCategorie()));

        SousCategorie sousCategorie = new SousCategorie();
        sousCategorie.setNom(command.getNom());
        sousCategorie.setDescription(command.getDescription());
        sousCategorie.setCategorie(categorie);
        SousCategorie savedSousCategorie = sousCategorieRepositoryService.saveSousCategorie(sousCategorie);
        return new AddSousCategorieResponse(
                savedSousCategorie.getIdSousCategorie(),
                "Sous-catégorie créée avec succès."
        );
    }
}
