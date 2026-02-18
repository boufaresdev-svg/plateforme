package com.example.BacK.application.g_Projet.Command.charge.delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.ChargeRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteChargeHandler")
public class DeleteChargeHandler implements RequestHandler<DeleteChargeCommand, Void> {

    private final ChargeRepositoryService chargeRepositoryService;

    public DeleteChargeHandler(ChargeRepositoryService chargeRepositoryService) {
        this.chargeRepositoryService = chargeRepositoryService;
    }

    @Override
    public Void handle(DeleteChargeCommand command) {
        chargeRepositoryService.delete(command.getId());
        return null;
    }
}