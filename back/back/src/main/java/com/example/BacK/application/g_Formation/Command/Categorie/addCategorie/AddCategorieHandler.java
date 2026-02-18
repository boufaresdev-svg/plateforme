package com.example.BacK.application.g_Formation.Command.Categorie.addCategorie;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddCategorieHandler")
public class AddCategorieHandler implements RequestHandler<AddCategorieCommand, AddCategorieResponse> {

    private final CategorieRepositoryService categorieRepositoryService;
    private final TypeRepositoryService typeRepositoryService;
    private final ModelMapper modelMapper;

    public AddCategorieHandler(CategorieRepositoryService categorieRepositoryService,
                               TypeRepositoryService typeRepositoryService,
                               ModelMapper modelMapper) {
        this.categorieRepositoryService = categorieRepositoryService;
        this.typeRepositoryService = typeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCategorieResponse handle(AddCategorieCommand command) {
        if (command.getNom() == null || command.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide.");
        }

        // Validate existence without attaching a potentially detached entity across transactions
        if (!typeRepositoryService.existsById(command.getIdType())) {
            throw new IllegalArgumentException("Type introuvable avec l'ID : " + command.getIdType());
        }

        Categorie categorie = new Categorie();
        categorie.setNom(command.getNom());
        categorie.setDescription(command.getDescription());
        // Set only the Type identifier to avoid detached entity issues during persist
        Type typeRef = new Type();
        typeRef.setIdType(command.getIdType());
        categorie.setType(typeRef);

        Categorie savedCategorie = categorieRepositoryService.saveCategorie(categorie);

        return new AddCategorieResponse(savedCategorie.getIdCategorie(), "Catégorie ajoutée avec succès !");
    }
}

