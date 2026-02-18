package com.example.BacK.application.g_Formation.Query.Type.TypesByDomaine;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.DomaineDTO;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetTypesByDomaineHandler")
public class GetTypesByDomaineHandler implements RequestHandler<GetTypesByDomaineQuery, List<GetTypesByDomaineResponse>> {

    private final TypeRepositoryService typeRepositoryService;

    public GetTypesByDomaineHandler(TypeRepositoryService typeRepositoryService) {
        this.typeRepositoryService = typeRepositoryService;
    }

    @Override
    public List<GetTypesByDomaineResponse> handle(GetTypesByDomaineQuery query) {
        List<Type> types = typeRepositoryService.findByDomaine(query.getIdDomaine());

        return types.stream()
                .map(type -> {
                    Domaine domaine = type.getDomaine();

                    DomaineDTO domaineDTO = null;
                    if (domaine != null) {
                        domaineDTO = new DomaineDTO(
                                domaine.getIdDomaine(),
                                domaine.getNom(),
                                domaine.getDescription()
                        );
                    }

                    return new GetTypesByDomaineResponse(
                            type.getIdType(),
                            type.getNom(),
                            type.getDescription(),
                            domaineDTO
                    );
                })
                .collect(Collectors.toList());
    }
}
