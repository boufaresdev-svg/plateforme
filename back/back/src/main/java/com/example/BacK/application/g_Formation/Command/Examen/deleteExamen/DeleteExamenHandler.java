package com.example.BacK.application.g_Formation.Command.Examen.deleteExamen;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteExamenHandler")
public class DeleteExamenHandler implements RequestHandler<DeleteExamenCommand, Void> {

    private final ExamenRepositoryService examenRepositoryService;

    public DeleteExamenHandler(ExamenRepositoryService examenRepositoryService) {
        this.examenRepositoryService = examenRepositoryService;
    }

    @Override
    public Void handle(DeleteExamenCommand command) {
        if (command.getIdExamen() == null) {
            throw new IllegalArgumentException("L’ID de l’examen ne peut pas être nul.");
        }

        boolean exists = examenRepositoryService.existsById(command.getIdExamen());
        if (!exists) {
            throw new IllegalArgumentException("Aucun examen trouvé avec l’ID : " + command.getIdExamen());
        }

        examenRepositoryService.deleteExamen(command.getIdExamen());
        return null;
    }
}
