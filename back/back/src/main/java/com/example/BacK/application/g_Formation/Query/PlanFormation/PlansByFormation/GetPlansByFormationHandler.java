package com.example.BacK.application.g_Formation.Query.PlanFormation.PlansByFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.FormateurDTO;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetPlansByFormationHandler")
public class GetPlansByFormationHandler implements RequestHandler<GetPlansByFormationQuery, List<GetPlansByFormationResponse>> {

    private final PlanFormationRepositoryService planFormationRepository;

    public GetPlansByFormationHandler(PlanFormationRepositoryService planFormationRepository) {
        this.planFormationRepository = planFormationRepository;
    }

    @Override
    public List<GetPlansByFormationResponse> handle(GetPlansByFormationQuery query) {

        List<PlanFormation> plans = planFormationRepository.findByFormationId(query.getIdFormation());

        return plans.stream().map(plan -> {

            FormateurDTO formateurDTO = null;
            if (plan.getFormateur() != null) {
                formateurDTO = new FormateurDTO(
                        plan.getFormateur().getIdFormateur(),
                        plan.getFormateur().getNom(),
                        plan.getFormateur().getPrenom(),
                        plan.getFormateur().getSpecialite(),
                        plan.getFormateur().getContact(),
                        plan.getFormateur().getExperience(),
                        plan.getFormateur().getDocumentUrl()
                );
            }

            return new GetPlansByFormationResponse(
                    plan.getIdPlanFormation(),
                    plan.getTitre(),
                    plan.getDescription(),
                    plan.getDateDebut(),
                    plan.getDateFin(),
                    plan.getDateLancement(),
                    plan.getDateFinReel(),
                    plan.getStatusFormation() != null ? plan.getStatusFormation().name() : null,
                    plan.getFormation() != null ? plan.getFormation().getIdFormation() : null,
                    formateurDTO,
                    plan.getNombreJours()
            );
        }).collect(Collectors.toList());
    }
}
