package com.example.BacK.application.g_Vehicule.Command.carteTelepeage.deleteCarteTelepeage;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Vehicule.CarteTelepeageRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteCarteTelepeageHandler")
public class DeleteCarteTelepeageHandler implements RequestHandler<DeleteCarteTelepeageCommand, Void> {

    private final CarteTelepeageRepositoryService carteTelepeageRepositoryService;

    public DeleteCarteTelepeageHandler(CarteTelepeageRepositoryService carteTelepeageRepositoryService) {
        this.carteTelepeageRepositoryService = carteTelepeageRepositoryService;
    }

    @Override
    public Void handle(DeleteCarteTelepeageCommand command) {
        this.carteTelepeageRepositoryService.delete(command.getId());
        return null;
    }
}