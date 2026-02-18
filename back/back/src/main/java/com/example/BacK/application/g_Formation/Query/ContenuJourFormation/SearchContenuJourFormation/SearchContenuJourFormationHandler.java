package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.SearchContenuJourFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("SearchContenuJourFormationHandler")
public class SearchContenuJourFormationHandler implements RequestHandler<SearchContenuJourFormationQuery, List<SearchContenuJourFormationResponse>> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ModelMapper modelMapper;

    public SearchContenuJourFormationHandler(ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
                                             ModelMapper modelMapper) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SearchContenuJourFormationResponse> handle(SearchContenuJourFormationQuery query) {
        List<ContenuJourFormation> contenus;
        
        // If searching by objectif specifique, filter by it
        if (query.getIdObjectifSpecifique() != null) {
            contenus = contenuJourFormationRepositoryService.findByObjectif(query.getIdObjectifSpecifique());
        } else {
            contenus = contenuJourFormationRepositoryService.getAllContenusJourFormation();
        }

        // Filter by contenu text if provided
        if (query.getContenu() != null && !query.getContenu().isEmpty()) {
            String searchTerm = query.getContenu().toLowerCase();
            contenus = contenus.stream()
                    .filter(c -> c.getContenu() != null && c.getContenu().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }
        
        // Filter by isCopied status if specified
        if (query.getIsCopied() != null) {
            contenus = contenus.stream()
                    .filter(c -> {
                        boolean hasCopiedLink = c.getObjectifSpecifique() != null;
                        return query.getIsCopied() == hasCopiedLink;
                    })
                    .collect(Collectors.toList());
        }

        // Map all results to responses
        return contenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SearchContenuJourFormationResponse mapToResponse(ContenuJourFormation contenu) {
        SearchContenuJourFormationResponse response = modelMapper.map(contenu, SearchContenuJourFormationResponse.class);
        
        response.setNumeroJour(contenu.getNumeroJour());
        response.setStaff(contenu.getStaff());
        response.setNiveau(contenu.getNiveau());
        
        if (contenu.getObjectifSpecifique() != null) {
            response.setObjectifSpecifiqueId(contenu.getObjectifSpecifique().getIdObjectifSpec());
            response.setObjectifSpecifiqueTitre(contenu.getObjectifSpecifique().getTitre());
            response.setCopied(true);
        } else {
            response.setCopied(false);
        }
        
        if (contenu.getPlanFormation() != null) {
            response.setPlanFormationId(contenu.getPlanFormation().getIdPlanFormation());
        }
        
        return response;
    }
}
