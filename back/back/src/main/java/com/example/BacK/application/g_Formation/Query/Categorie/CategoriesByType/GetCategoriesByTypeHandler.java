package com.example.BacK.application.g_Formation.Query.Categorie.CategoriesByType;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.TypeDTO;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.CategorieRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetCategoriesByTypeHandler")
public class GetCategoriesByTypeHandler implements RequestHandler<GetCategoriesByTypeQuery, List<GetCategoriesByTypeResponse>> {

    private final CategorieRepositoryService categorieRepositoryService;

    public GetCategoriesByTypeHandler(CategorieRepositoryService categorieRepositoryService) {
        this.categorieRepositoryService = categorieRepositoryService;
    }

    @Override
    public List<GetCategoriesByTypeResponse> handle(GetCategoriesByTypeQuery query) {

        List<Categorie> categories = categorieRepositoryService.findByType(query.getIdtype());

        return categories.stream()
                .map(cat -> {
                    Type type = cat.getType();

                    TypeDTO typeDTO = null;
                    if (type != null) {
                        typeDTO = new TypeDTO(
                                type.getIdType(),
                                type.getNom(),
                                type.getDescription(),
                                type.getDomaine() != null ? type.getDomaine().getIdDomaine() : null
                        );
                    }

                    return new GetCategoriesByTypeResponse(
                            cat.getIdCategorie(),
                            cat.getNom(),
                            cat.getDescription(),
                            typeDTO
                    );
                })
                .collect(Collectors.toList());
    }
}

