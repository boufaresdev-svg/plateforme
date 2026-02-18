package com.example.BacK.application.g_Formation.Query.SousCategorie.SousCategoriesByCategorie;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.CategorieDTO;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.SousCategorie;
import com.example.BacK.infrastructure.services.g_Formation.SousCategorieRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetSousCategoriesByCategorieHandler")
public class GetSousCategoriesByCategorieHandler implements RequestHandler<GetSousCategoriesByCategorieQuery, List<GetSousCategoriesByCategorieResponse>> {

    private final SousCategorieRepositoryService sousCategorieRepositoryService;

    public GetSousCategoriesByCategorieHandler(SousCategorieRepositoryService sousCategorieRepositoryService) {
        this.sousCategorieRepositoryService = sousCategorieRepositoryService;
    }

    @Override
    public List<GetSousCategoriesByCategorieResponse> handle(GetSousCategoriesByCategorieQuery query) {

        List<SousCategorie> sousCategories = sousCategorieRepositoryService.findByCategorie(query.getIdCategorie());

        return sousCategories.stream()
                .map(sc -> {
                    Categorie cat = sc.getCategorie();

                    CategorieDTO categorieDTO = null;
                    if (cat != null) {
                        categorieDTO = new CategorieDTO(
                                cat.getIdCategorie(),
                                cat.getNom(),
                                cat.getDescription(),
                                cat.getType() != null ? cat.getType().getIdType() : null
                        );
                    }

                    return new GetSousCategoriesByCategorieResponse(
                            sc.getIdSousCategorie(),
                            sc.getNom(),
                            sc.getDescription(),
                            categorieDTO
                    );
                })
                .collect(Collectors.toList());
    }
}
