package com.example.BacK.application.g_Formation.Query.ContenuJourFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetContenuJourFormationHandler")
public class GetContenuJourFormationHandler implements RequestHandler<GetContenuJourFormationQuery, List<GetContenuJourFormationResponse>> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ModelMapper modelMapper;

    public GetContenuJourFormationHandler(
            ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
            ModelMapper modelMapper) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetContenuJourFormationResponse> handle(GetContenuJourFormationQuery query) {

        if (query.getIdContenuJour() != null) {

            ContenuJourFormation contenu = contenuJourFormationRepositoryService
                    .getContenuJourFormationById(query.getIdContenuJour())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun contenu de jour trouvé avec l’ID : " + query.getIdContenuJour()
                    ));

            return List.of(mapToResponse(contenu));
        }

        List<ContenuJourFormation> allContenus =
                contenuJourFormationRepositoryService.getAllContenusJourFormation();

        return allContenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetContenuJourFormationResponse mapToResponse(ContenuJourFormation contenu) {

        GetContenuJourFormationResponse response = new GetContenuJourFormationResponse();
        
        response.setIdContenuJour(contenu.getIdContenuJour());
        response.setContenu(contenu.getContenu());
        response.setMoyenPedagogique(contenu.getMoyenPedagogique());
        response.setSupportPedagogique(contenu.getSupportPedagogique());
        response.setNbHeuresTheoriques(contenu.getNbHeuresTheoriques());
        response.setNbHeuresPratiques(contenu.getNbHeuresPratiques());
        response.setNumeroJour(contenu.getNumeroJour());
        response.setStaff(contenu.getStaff());
        response.setNiveau(contenu.getNiveau());

        if (contenu.getObjectifSpecifique() != null) {
            response.setObjectifSpecifiqueId(
                    contenu.getObjectifSpecifique().getIdObjectifSpec()
            );
        }

        if (contenu.getPlanFormation() != null) {
            response.setIdPlanFormation(
                    contenu.getPlanFormation().getIdPlanFormation()
            );
        }

        // Include assigned contenu detaille IDs
        if (contenu.getContenusDetailles() != null && !contenu.getContenusDetailles().isEmpty()) {
            response.setAssignedContenuDetailleIds(
                    contenu.getContenusDetailles().stream()
                            .map(cd -> cd.getIdContenuDetaille())
                            .collect(Collectors.toList())
            );
        }

        return response;
    }
}
