package com.example.BacK.application.g_Formation.Command.Formation.deleteFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteFormationHandler")
public class DeleteFormationHandler implements RequestHandler<DeleteFormationCommand, Void> {

    private final FormationRepositoryService formationRepositoryService;

    public DeleteFormationHandler(FormationRepositoryService formationRepositoryService) {
        this.formationRepositoryService = formationRepositoryService;
    }

    @Override
    public Void handle(DeleteFormationCommand command) {

        boolean exists = formationRepositoryService.existsById(command.getIdFormation());
        if (!exists) {
            throw new IllegalArgumentException(
                    "La formation avec l'ID " + command.getIdFormation() + " n'existe pas."
            );
        }

        formationRepositoryService.deleteFormation(command.getIdFormation());

        return null;
    }
}

