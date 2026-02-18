package com.example.BacK.application.g_Formation.Command.ContenuDetaille.deleteContenuDetaille;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteContenuDetailleHandler")
public class DeleteContenuDetailleHandler implements RequestHandler<DeleteContenuDetailleCommand, DeleteContenuDetailleResponse> {

    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;

    public DeleteContenuDetailleHandler(ContenuDetailleRepositoryService contenuDetailleRepositoryService) {
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
    }

    @Override
    public DeleteContenuDetailleResponse handle(DeleteContenuDetailleCommand command) {
        // Validate contenu exists
        if (!contenuDetailleRepositoryService.existsById(command.getIdContenuDetaille())) {
            throw new IllegalArgumentException(
                    "ContenuDetaille non trouvé avec l'ID : " + command.getIdContenuDetaille()
            );
        }

        Long idToDelete = command.getIdContenuDetaille();
        
        // Delete
        contenuDetailleRepositoryService.deleteContenuDetaille(idToDelete);

        return new DeleteContenuDetailleResponse(
                idToDelete,
                "ContenuDetaille supprimé avec succès !"
        );
    }
}
