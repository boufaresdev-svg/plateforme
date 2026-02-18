package com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAll;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetAllContenuDetailleHandler")
public class GetAllContenuDetailleHandler implements RequestHandler<GetAllContenuDetailleQuery, List<GetAllContenuDetailleResponse>> {

    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;
    private final ModelMapper modelMapper;

    public GetAllContenuDetailleHandler(
            ContenuDetailleRepositoryService contenuDetailleRepositoryService,
            ModelMapper modelMapper) {
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetAllContenuDetailleResponse> handle(GetAllContenuDetailleQuery query) {
        List<ContenuDetaille> contenus = contenuDetailleRepositoryService.getAllContenuDetaille();
        
        return contenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetAllContenuDetailleResponse mapToResponse(ContenuDetaille contenuDetaille) {
        GetAllContenuDetailleResponse response = new GetAllContenuDetailleResponse();
        response.setIdContenuDetaille(contenuDetaille.getIdContenuDetaille());
        response.setTitre(contenuDetaille.getTitre());
        response.setContenusCles(contenuDetaille.getContenusCles());
        response.setMethodesPedagogiques(contenuDetaille.getMethodesPedagogiques());
        response.setDureeTheorique(contenuDetaille.getDureeTheorique());
        response.setDureePratique(contenuDetaille.getDureePratique());
        response.setTags(contenuDetaille.getTags());
        response.setLevels(contenuDetaille.getLevels());
        
        // Safely map idJourFormation if exists (avoiding lazy loading issues)
        try {
            if (contenuDetaille.getJourFormation() != null) {
                response.setIdJourFormation(contenuDetaille.getJourFormation().getIdJour());
            }
        } catch (Exception e) {
            // JourFormation might be lazy-loaded and session closed
            response.setIdJourFormation(null);
        }
        
        return response;
    }
}
