package com.example.BacK.application.g_Formation.Query.ContenuGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetContenuGlobalHandler")
public class GetContenuGlobalHandler implements RequestHandler<GetContenuGlobalQuery, List<GetContenuGlobalResponse>> {

    private final ContenuGlobalRepositoryService contenuGlobalRepositoryService;
    private final ModelMapper modelMapper;

    public GetContenuGlobalHandler(ContenuGlobalRepositoryService contenuGlobalRepositoryService,
                                   ModelMapper modelMapper) {
        this.contenuGlobalRepositoryService = contenuGlobalRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetContenuGlobalResponse> handle(GetContenuGlobalQuery query) {

        if (query.getIdContenuGlobal() != null) {
            ContenuGlobal contenu = contenuGlobalRepositoryService.getContenuGlobalById(query.getIdContenuGlobal())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun contenu global trouvé avec l’ID : " + query.getIdContenuGlobal()
                    ));

            return List.of(mapToResponse(contenu));
        }

        List<ContenuGlobal> contenus = contenuGlobalRepositoryService.getAllContenusGlobaux();

        return contenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetContenuGlobalResponse mapToResponse(ContenuGlobal contenu) {
        GetContenuGlobalResponse response = modelMapper.map(contenu, GetContenuGlobalResponse.class);

        if (contenu.getFormation() != null) {
            response.setFormationid(contenu.getFormation().getIdFormation());
        }
        return response;
    }
}
