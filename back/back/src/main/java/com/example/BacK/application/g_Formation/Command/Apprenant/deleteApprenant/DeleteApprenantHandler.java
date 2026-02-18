package com.example.BacK.application.g_Formation.Command.Apprenant.deleteApprenant;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteApprenantHandler")
public class DeleteApprenantHandler implements RequestHandler<DeleteApprenantCommand, Void> {

    private final ApprenantRepositoryService apprenantRepositoryService;

    public DeleteApprenantHandler(ApprenantRepositoryService apprenantRepositoryService) {
        this.apprenantRepositoryService = apprenantRepositoryService;
    }

    @Override
    public Void handle(DeleteApprenantCommand command) {
        Long id = command.getIdApprenant();

        if (!apprenantRepositoryService.existsById(id)) {
            throw new IllegalArgumentException("Aucun apprenant trouvé avec l’ID : " + id);
        }

        apprenantRepositoryService.deleteApprenant(id);
        return null;
    }
}