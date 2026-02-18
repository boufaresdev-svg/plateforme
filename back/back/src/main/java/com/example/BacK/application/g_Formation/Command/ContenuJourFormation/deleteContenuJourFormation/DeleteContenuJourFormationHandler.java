package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.deleteContenuJourFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteContenuJourFormationHandler")
public class DeleteContenuJourFormationHandler implements RequestHandler<DeleteContenuJourFormationCommand, Void> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;

    public DeleteContenuJourFormationHandler(ContenuJourFormationRepositoryService contenuJourFormationRepositoryService) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
    }

    @Override
    public Void handle(DeleteContenuJourFormationCommand command) {

        Long id = command.getIdContenuJour();

        if (!contenuJourFormationRepositoryService.existsById(id)) {
            throw new IllegalArgumentException("Aucun contenu de jour de formation trouvé avec l'ID : " + id);
        }

        contenuJourFormationRepositoryService.deleteContenuJourFormation(id);

        return null;
    }
}
