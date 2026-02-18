package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.ContenusByObjectif;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.ObjectifSpecifiqueDTO;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetContenusByObjectifHandler")
public class GetContenusByObjectifHandler implements RequestHandler<GetContenusByObjectifQuery, List<GetContenusByObjectifResponse>> {

    private final ContenuJourFormationRepositoryService service;

    public GetContenusByObjectifHandler(ContenuJourFormationRepositoryService service) {
        this.service = service;
    }

    @Override
    public List<GetContenusByObjectifResponse> handle(GetContenusByObjectifQuery query) {

        List<ContenuJourFormation> contenus = service.findByObjectif(query.getIdObjectifSpec());

        return contenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetContenusByObjectifResponse mapToResponse(ContenuJourFormation c) {

        ObjectifSpecifiqueDTO objectifDTO = new ObjectifSpecifiqueDTO(
                c.getObjectifSpecifique().getIdObjectifSpec(),
                c.getObjectifSpecifique().getTitre(),
                c.getObjectifSpecifique().getDescription()
        );

        GetContenusByObjectifResponse dto = new GetContenusByObjectifResponse();
        dto.setIdContenuJour(c.getIdContenuJour());
        dto.setContenu(c.getContenu());
        dto.setMoyenPedagogique(c.getMoyenPedagogique());
        dto.setSupportPedagogique(c.getSupportPedagogique());
        dto.setNbHeuresTheoriques(c.getNbHeuresTheoriques());
        dto.setNbHeuresPratiques(c.getNbHeuresPratiques());
        dto.setObjectifSpecifique(objectifDTO);
        dto.setNumeroJour(c.getNumeroJour());
        dto.setStaff(c.getStaff());
        dto.setNiveau(c.getNiveau());

        if (c.getPlanFormation() != null) {
            dto.setIdPlanFormation(c.getPlanFormation().getIdPlanFormation());
        } else {
            dto.setIdPlanFormation(null);
        }

        return dto;
    }
}
