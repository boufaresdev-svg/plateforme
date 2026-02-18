package com.example.BacK.application.g_Formation.Command.ContenuGlobal.deleteContenuGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteContenuGlobalHandler")
public class DeleteContenuGlobalHandler implements RequestHandler<DeleteContenuGlobalCommand, Void> {

    private final ContenuGlobalRepositoryService contenuGlobalRepositoryService;

    public DeleteContenuGlobalHandler(ContenuGlobalRepositoryService contenuGlobalRepositoryService) {
        this.contenuGlobalRepositoryService = contenuGlobalRepositoryService;
    }

    @Override
    public Void handle(DeleteContenuGlobalCommand command) {
        if (command.getIdContenuGlobal() == null) {
            throw new IllegalArgumentException("L’ID du contenu global ne peut pas être nul.");
        }

        boolean exists = contenuGlobalRepositoryService.existsById(command.getIdContenuGlobal());
        if (!exists) {
            throw new IllegalArgumentException(
                    "Aucun contenu global trouvé avec l’ID : " + command.getIdContenuGlobal()
            );
        }

        contenuGlobalRepositoryService.deleteContenuGlobal(command.getIdContenuGlobal());
        return null;
    }
}
