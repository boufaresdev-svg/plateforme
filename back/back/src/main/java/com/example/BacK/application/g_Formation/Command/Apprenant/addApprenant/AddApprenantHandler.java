package com.example.BacK.application.g_Formation.Command.Apprenant.addApprenant;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("AddApprenantHandler")
public class AddApprenantHandler implements RequestHandler<AddApprenantCommand, AddApprenantResponse> {

    private final ApprenantRepositoryService apprenantRepositoryService;
    private final PlanFormationRepositoryService planFormationRepositoryService;
    private final ModelMapper modelMapper;

    public AddApprenantHandler(ApprenantRepositoryService apprenantRepositoryService,
                               PlanFormationRepositoryService planFormationRepositoryService,
                               ModelMapper modelMapper) {
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.planFormationRepositoryService = planFormationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddApprenantResponse handle(AddApprenantCommand command) {

        Apprenant apprenant = modelMapper.map(command, Apprenant.class);

        if (command.getIdPlanFormation() != null) {
            PlanFormation planFormation = planFormationRepositoryService
                    .getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun plan de formation trouvé avec l'ID : " + command.getIdPlanFormation()));

            apprenant.setPlanFormation(planFormation);
        }

        Apprenant saved = apprenantRepositoryService.saveApprenant(apprenant);

        return new AddApprenantResponse(
                saved.getId(),
                "Apprenant ajouté avec succès !"
        );
    }
}
