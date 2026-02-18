package com.example.BacK.application.g_Stock.Command.entrepot.deleteEntrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteEntrepotHandler")
public class DeleteEntrepotHandler implements RequestHandler<DeleteEntrepotCommand, DeleteEntrepotResponse> {

    private final IEntrepotRepositoryService entrepotRepositoryService;

    public DeleteEntrepotHandler(IEntrepotRepositoryService entrepotRepositoryService) {
        this.entrepotRepositoryService = entrepotRepositoryService;
    }

    @Override
    public DeleteEntrepotResponse handle(DeleteEntrepotCommand command) {
        if (!entrepotRepositoryService.existsById(command.getId())) {
            throw new RuntimeException("Entrepôt non trouvé avec l'ID: " + command.getId());
        }

        entrepotRepositoryService.deleteById(command.getId());

        return new DeleteEntrepotResponse("Entrepôt supprimé avec succès");
    }
}
