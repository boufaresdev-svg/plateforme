package com.example.BacK.application.g_Formation.Query.Categorie;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("GetCategorieHandler")
public class GetCategorieHandler implements RequestHandler<GetCategorieQuery, List<GetCategorieResponse>> {

    private final CategorieRepositoryService categorieRepositoryService;

    public GetCategorieHandler(CategorieRepositoryService categorieRepositoryService) {
        this.categorieRepositoryService = categorieRepositoryService;
    }

    @Override
    public List<GetCategorieResponse> handle(GetCategorieQuery query) {
        try {
            if (query.getIdCategorie() != null) {
                Categorie categorie = categorieRepositoryService.getCategorieById(query.getIdCategorie())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Catégorie non trouvée avec l'ID : " + query.getIdCategorie()));

                GetCategorieResponse response = convertToResponse(categorie);
                return List.of(response);
            }

                List<Categorie> categories = categorieRepositoryService.getAllCategories();
                return categories.stream()
                    .filter(Objects::nonNull)
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching categories: " + e.getMessage(), e);
        }
    }


    private GetCategorieResponse convertToResponse(Categorie categorie) {
        if (categorie == null) {
            throw new IllegalArgumentException("Categorie cannot be null");
        }
        
        try {
            GetCategorieResponse response = new GetCategorieResponse();
            response.setIdCategorie(categorie.getIdCategorie());
            response.setNom(categorie.getNom());
            response.setDescription(categorie.getDescription());

            // Handle lazy-loaded Type safely
            try {
                if (categorie.getType() != null) {
                    response.setIdType(categorie.getType().getIdType());
                    GetCategorieResponse.TypeInfo typeInfo = new GetCategorieResponse.TypeInfo();
                    typeInfo.setIdType(categorie.getType().getIdType());
                    typeInfo.setNom(categorie.getType().getNom());
                    response.setType(typeInfo);
                }
            } catch (Exception e) {
                response.setIdType(null);
                response.setType(null);
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error converting category: " + e.getMessage(), e);
        }
    }
}

