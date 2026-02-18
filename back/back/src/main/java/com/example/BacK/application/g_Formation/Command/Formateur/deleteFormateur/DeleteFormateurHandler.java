package com.example.BacK.application.g_Formation.Command.Formateur.deleteFormateur;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteFormateurHandler")
public class DeleteFormateurHandler implements RequestHandler<DeleteFormateurCommand, Void> {

    private final FormateurRepositoryService formateurRepositoryService;

    public DeleteFormateurHandler(FormateurRepositoryService formateurRepositoryService) {
        this.formateurRepositoryService = formateurRepositoryService;
    }

    @Override
    public Void handle(DeleteFormateurCommand command) {
        if (command.getIdFormateur() == null) {
            throw new IllegalArgumentException("L'ID du formateur à supprimer ne peut pas être nul.");
        }

        boolean exists = formateurRepositoryService.exists(command.getIdFormateur());
        if (!exists) {
            throw new IllegalArgumentException("Le formateur avec l'ID " + command.getIdFormateur() + " n'existe pas.");
        }

        formateurRepositoryService.deleteFormateur(command.getIdFormateur());

        return null;
    }
}
