package com.example.BacK.application.g_Projet.Command.Tache.Delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteTacheHandler")
public class DeleteTacheHandler implements RequestHandler<DeleteTacheCommand, Void> {

    private final TacheRepositoryService tacheRepositoryService;

    public DeleteTacheHandler(TacheRepositoryService tacheRepositoryService) {
        this.tacheRepositoryService = tacheRepositoryService;
    }

    @Override
    public Void handle(DeleteTacheCommand command) {
        tacheRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}
