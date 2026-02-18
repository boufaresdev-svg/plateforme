package com.example.BacK.application.g_Formation.Query.PlanFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetPlanFormationHandler")
public class GetPlanFormationHandler implements RequestHandler<GetPlanFormationQuery, List<GetPlanFormationResponse>> {

    private final PlanFormationRepositoryService planFormationRepositoryService;
    private final ModelMapper modelMapper;

    public GetPlanFormationHandler(PlanFormationRepositoryService planFormationRepositoryService, ModelMapper modelMapper) {
        this.planFormationRepositoryService = planFormationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetPlanFormationResponse> handle(GetPlanFormationQuery command) {

        if (command.getIdPlanFormation() != null) {
            PlanFormation plan = planFormationRepositoryService.getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun plan de formation trouvé avec l’ID : " + command.getIdPlanFormation()
                    ));
            return List.of(mapToResponse(plan));
        }

        List<PlanFormation> plans = planFormationRepositoryService.getAllPlanFormations();
        return plans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetPlanFormationResponse mapToResponse(PlanFormation plan) {
        modelMapper.typeMap(PlanFormation.class, GetPlanFormationResponse.class)
                .addMappings(mapper -> {
                    mapper.skip(GetPlanFormationResponse::setFormationId);
                    mapper.skip(GetPlanFormationResponse::setFormateurId);
                });

        GetPlanFormationResponse response = modelMapper.map(plan, GetPlanFormationResponse.class);

        if (plan.getFormation() != null) {
            response.setFormationId(plan.getFormation().getIdFormation());
        }

        if (plan.getFormateur() != null) {
            response.setFormateurId(plan.getFormateur().getIdFormateur());
        }

        response.setIdPlanFormation(plan.getIdPlanFormation());
        response.setNombreJours(plan.getNombreJours());

        return response;
    }
}
