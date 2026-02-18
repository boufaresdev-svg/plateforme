package com.example.BacK.application.g_Formation.Query.ObjectifSpecifique;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetObjectifSpecifiqueHandler")
public class GetObjectifSpecifiqueHandler implements RequestHandler<GetObjectifSpecifiqueQuery, List<GetObjectifSpecifiqueResponse>> {

    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final ModelMapper modelMapper;

    public GetObjectifSpecifiqueHandler(ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
                                        ModelMapper modelMapper) {
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetObjectifSpecifiqueResponse> handle(GetObjectifSpecifiqueQuery command) {

        if (command.getIdObjectifSpecifique() != null) {
            ObjectifSpecifique objectif = objectifSpecifiqueRepositoryService
                    .getObjectifSpecifiqueById(command.getIdObjectifSpecifique())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun objectif spécifique trouvé avec l’ID : " + command.getIdObjectifSpecifique()
                    ));

            GetObjectifSpecifiqueResponse response = mapToResponse(objectif);
            return List.of(response);
        }

        List<ObjectifSpecifique> objectifs = objectifSpecifiqueRepositoryService.getAllObjectifsSpecifiques();

        return objectifs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetObjectifSpecifiqueResponse mapToResponse(ObjectifSpecifique objectif) {
        GetObjectifSpecifiqueResponse response = modelMapper.map(objectif, GetObjectifSpecifiqueResponse.class);

        if (objectif.getContenuGlobal() != null) {
            response.setContenuGlobalid(objectif.getContenuGlobal().getIdContenuGlobal());
        }

        if (objectif.getObjectifGlobal() != null) {
            response.setObjectifGlobalId(objectif.getObjectifGlobal().getIdObjectifGlobal());
        }

        return response;
    }
}
