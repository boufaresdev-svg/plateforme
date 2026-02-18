package com.example.BacK.application.g_Formation.Query.SousCategorie;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.SousCategorie;
import com.example.BacK.infrastructure.services.g_Formation.SousCategorieRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component("GetSousCategorieHandler")
public class GetSousCategorieHandler implements RequestHandler<GetSousCategorieQuery, List<GetSousCategorieResponse>> {

    private final SousCategorieRepositoryService sousCategorieRepositoryService;

    public GetSousCategorieHandler(SousCategorieRepositoryService sousCategorieRepositoryService) {
        this.sousCategorieRepositoryService = sousCategorieRepositoryService;
    }

    @Override
    public List<GetSousCategorieResponse> handle(GetSousCategorieQuery query) {

        if (query.getIdSousCategorie() != null) {
            SousCategorie sousCategorie = sousCategorieRepositoryService
                    .getSousCategorieById(query.getIdSousCategorie())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Sous-catégorie introuvable avec l'ID : " + query.getIdSousCategorie()
                    ));

            return List.of(mapToResponse(sousCategorie));
        }


        return sousCategorieRepositoryService.getAllSousCategories().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private GetSousCategorieResponse mapToResponse(SousCategorie sousCategorie) {
        GetSousCategorieResponse response = new GetSousCategorieResponse();
        response.setIdSousCategorie(sousCategorie.getIdSousCategorie());
        response.setNom(sousCategorie.getNom());
        response.setDescription(sousCategorie.getDescription());
        
        if (sousCategorie.getCategorie() != null) {
            response.setIdCategorie(sousCategorie.getCategorie().getIdCategorie());
            GetSousCategorieResponse.CategorieInfo categorieInfo = new GetSousCategorieResponse.CategorieInfo();
            categorieInfo.setIdCategorie(sousCategorie.getCategorie().getIdCategorie());
            categorieInfo.setNom(sousCategorie.getCategorie().getNom());
            response.setCategorie(categorieInfo);
        }
        
        return response;
    }
}
