package com.example.BacK.application.g_Formation.Command.PlanFormation.deletePlanFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeletePlanFormationHandler")
public class DeletePlanFormationHandler implements RequestHandler<DeletePlanFormationCommand, Void> {

    private final PlanFormationRepositoryService planFormationRepositoryService;

    public DeletePlanFormationHandler(PlanFormationRepositoryService planFormationRepositoryService) {
        this.planFormationRepositoryService = planFormationRepositoryService;
    }

    @Override
    public Void handle(DeletePlanFormationCommand command) {
        if (command.getIdPlanFormation() == null) {
            throw new IllegalArgumentException("L'ID du plan de formation est requis pour la suppression.");
        }

        boolean exists = planFormationRepositoryService.existsById(command.getIdPlanFormation());
        if (!exists) {
            throw new IllegalArgumentException(
                    "Le plan de formation avec l'ID " + command.getIdPlanFormation() + " n'existe pas.");
        }

        // 🗑️ Suppression
        planFormationRepositoryService.deletePlanFormation(command.getIdPlanFormation());

        return null;
    }
}
