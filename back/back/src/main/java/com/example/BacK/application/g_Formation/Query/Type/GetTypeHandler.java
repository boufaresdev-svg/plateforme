package com.example.BacK.application.g_Formation.Query.Type;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.DomaineDTO;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;


@Component("GetTypeHandler")
public class GetTypeHandler implements RequestHandler<GetTypeQuery, List<GetTypeResponse>> {

    private final TypeRepositoryService typeRepositoryService;

    public GetTypeHandler(TypeRepositoryService typeRepositoryService) {
        this.typeRepositoryService = typeRepositoryService;
    }

    @Override
    public List<GetTypeResponse> handle(GetTypeQuery query) {
        List<Type> types = typeRepositoryService.getAllTypes();

        return types.stream()
                .map(type -> {
                    DomaineDTO domaineDTO = null;
                    if (type.getDomaine() != null) {
                        domaineDTO = new DomaineDTO(type.getDomaine().getIdDomaine(), type.getDomaine().getNom(), type.getDomaine().getDescription());
                    }
                    return new GetTypeResponse(type.getIdType(), type.getNom(), type.getDescription(), domaineDTO);
                })
                .collect(Collectors.toList());
    }
}
