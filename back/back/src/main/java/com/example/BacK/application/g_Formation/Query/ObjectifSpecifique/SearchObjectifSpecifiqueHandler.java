package com.example.BacK.application.g_Formation.Query.ObjectifSpecifique;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("SearchObjectifSpecifiqueHandler")
public class SearchObjectifSpecifiqueHandler implements RequestHandler<SearchObjectifSpecifiqueQuery, List<SearchObjectifSpecifiqueResponse>> {

    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final ModelMapper modelMapper;

    public SearchObjectifSpecifiqueHandler(ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
                                           ModelMapper modelMapper) {
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SearchObjectifSpecifiqueResponse> handle(SearchObjectifSpecifiqueQuery command) {
        List<ObjectifSpecifique> objectifs = objectifSpecifiqueRepositoryService.getAllObjectifsSpecifiques();

        // Filter by titre if provided
        if (command.getTitre() != null && !command.getTitre().isEmpty()) {
            String searchTerm = command.getTitre().toLowerCase();
            objectifs = objectifs.stream()
                    .filter(o -> o.getTitre() != null && o.getTitre().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }

        // Map all results to responses
        return objectifs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SearchObjectifSpecifiqueResponse mapToResponse(ObjectifSpecifique objectif) {
        SearchObjectifSpecifiqueResponse response = modelMapper.map(objectif, SearchObjectifSpecifiqueResponse.class);
        
        if (objectif.getObjectifGlobal() != null) {
            response.setObjectifGlobalId(objectif.getObjectifGlobal().getIdObjectifGlobal());
        }
        
        if (objectif.getContenuGlobal() != null) {
            response.setContenuGlobalId(objectif.getContenuGlobal().getIdContenuGlobal());
        }
        
        return response;
    }
}
