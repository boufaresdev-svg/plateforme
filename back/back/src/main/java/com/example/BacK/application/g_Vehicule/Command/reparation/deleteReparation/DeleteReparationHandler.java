package com.example.BacK.application.g_Vehicule.Command.reparation.deleteReparation;

import com.example.BacK.application.interfaces.g_Vehicule.Reparation.IReparationRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteReparationHandler")
public class DeleteReparationHandler implements RequestHandler<DeleteReparationCommand, Void> {

    private final IReparationRepositoryService reparationRepositoryService;

    public DeleteReparationHandler(IReparationRepositoryService reparationRepositoryService) {
        this.reparationRepositoryService = reparationRepositoryService;
    }

    @Override
    public Void handle(DeleteReparationCommand command) {
        this.reparationRepositoryService.delete(command.getId());
        return null;
    }
}
