package com.example.BacK.application.g_Projet.Command.projet.delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteProjetHandler")
public class DeleteProjetHandler implements RequestHandler<DeleteProjetCommand, Void> {

    private final ProjectRepositoryService projectRepositoryService;

    public DeleteProjetHandler(ProjectRepositoryService projectRepositoryService) {
        this.projectRepositoryService = projectRepositoryService;
    }

    @Override
    public Void handle(DeleteProjetCommand command) {
        projectRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}

