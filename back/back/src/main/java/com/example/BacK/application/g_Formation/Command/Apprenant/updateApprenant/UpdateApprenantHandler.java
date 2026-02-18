package com.example.BacK.application.g_Formation.Command.Apprenant.updateApprenant;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateApprenantHandler")
public class UpdateApprenantHandler implements RequestHandler<UpdateApprenantCommand, Void> {

    private final ApprenantRepositoryService apprenantRepositoryService;
    private final PlanFormationRepositoryService planFormationRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateApprenantHandler(ApprenantRepositoryService apprenantRepositoryService,
                                  PlanFormationRepositoryService planFormationRepositoryService,
                                  ModelMapper modelMapper) {
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.planFormationRepositoryService = planFormationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateApprenantCommand command) {
        Apprenant existing = apprenantRepositoryService.getApprenantById(command.getIdApprenant())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun apprenant trouvé avec l’ID : " + command.getIdApprenant()
                ));

        modelMapper.map(command, existing);

        if (command.getIdPlanFormation() != null) {
            PlanFormation planFormation = planFormationRepositoryService.getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Plan de formation non trouvé avec l’ID : " + command.getIdPlanFormation()
                    ));
            existing.setPlanFormation(planFormation);
        }

        apprenantRepositoryService.updateApprenant(command.getIdApprenant(), existing);

        return null;
    }
}
