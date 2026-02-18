package com.example.BacK.application.g_Projet.Command.phase.delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.PhaseRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeletePhaseHandler")
public class DeletePhaseHandler implements RequestHandler<DeletePhaseCommand, Void> {

    private final PhaseRepositoryService phaseRepositoryService;

    public DeletePhaseHandler(PhaseRepositoryService phaseRepositoryService) {
        this.phaseRepositoryService = phaseRepositoryService;
    }

    @Override
    public Void handle(DeletePhaseCommand command) {
        phaseRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}

