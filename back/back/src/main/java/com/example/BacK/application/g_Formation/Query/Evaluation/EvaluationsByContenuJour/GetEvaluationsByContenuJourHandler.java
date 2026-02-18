package com.example.BacK.application.g_Formation.Query.Evaluation.EvaluationsByContenuJour;

import com.example.BacK.application.interfaces.g_Formation.Evaluation.IEvaluationRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.ApprenantDTO;
import com.example.BacK.application.models.g_formation.ContenuJourFormationDTO;
import com.example.BacK.application.models.g_formation.PlanFormationDTO;
import com.example.BacK.domain.g_Formation.Evaluation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetEvaluationsByContenuJourHandler")
public class GetEvaluationsByContenuJourHandler implements
        RequestHandler<GetEvaluationsByContenuJourQuery, List<GetEvaluationsByContenuJourResponse>> {

    private final IEvaluationRepositoryService service;

    public GetEvaluationsByContenuJourHandler(IEvaluationRepositoryService service) {
        this.service = service;
    }

    @Override
    public List<GetEvaluationsByContenuJourResponse> handle(GetEvaluationsByContenuJourQuery query) {

        List<Evaluation> evaluations =
                service.findByContenuJourFormation(query.getIdContenuJour());

        return evaluations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private GetEvaluationsByContenuJourResponse mapToResponse(Evaluation e) {

        GetEvaluationsByContenuJourResponse dto = new GetEvaluationsByContenuJourResponse();

        dto.setIdEvaluation(e.getIdEvaluation());
        dto.setType(e.getType());
        dto.setDate(e.getDate());
        dto.setDescription(e.getDescription());
        dto.setScore(e.getScore());

        if (e.getPlanFormation() != null) {
            PlanFormationDTO pf = new PlanFormationDTO();
            pf.setIdPlanFormation(e.getPlanFormation().getIdPlanFormation());
            pf.setTitre(e.getPlanFormation().getTitre());
            pf.setDescription(e.getPlanFormation().getDescription());
            pf.setDateDebut(e.getPlanFormation().getDateDebut());
            pf.setDateFin(e.getPlanFormation().getDateFin());
            pf.setStatusFormation(e.getPlanFormation().getStatusFormation());
            dto.setPlanFormationDTO(pf);
        }

        if (e.getContenuJourFormation() != null) {
            ContenuJourFormationDTO cj = new ContenuJourFormationDTO();
            cj.setIdContenuJour(e.getContenuJourFormation().getIdContenuJour());
            cj.setContenu(e.getContenuJourFormation().getContenu());
            cj.setMoyenPedagogique(e.getContenuJourFormation().getMoyenPedagogique());
            cj.setSupportPedagogique(e.getContenuJourFormation().getSupportPedagogique());
            cj.setNbHeuresTheoriques(e.getContenuJourFormation().getNbHeuresTheoriques());
            cj.setNbHeuresPratiques(e.getContenuJourFormation().getNbHeuresPratiques());
            dto.setContenuJourFormationDTO(cj);
        }

        if (e.getApprenant() != null) {
            dto.setIdApprenant(e.getApprenant().getId());
            ApprenantDTO ap = new ApprenantDTO();
            ap.setIdApprenant(e.getApprenant().getId());
            ap.setNom(e.getApprenant().getNom());
            ap.setPrenom(e.getApprenant().getPrenom());
            ap.setEmail(e.getApprenant().getEmail());
            ap.setTelephone(e.getApprenant().getTelephone());
            dto.setApprenantDTO(ap);
        }

        return dto;
    }
}
