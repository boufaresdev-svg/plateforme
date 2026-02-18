package com.example.BacK.application.g_Client.Command.client.DeleteClient;

import com.example.BacK.application.interfaces.g_Client.client.IClientRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteClientHandler")
public class DeleteClientHandler implements RequestHandler<DeleteClientCommand, Void> {

    private final IClientRepositoryService clientRepositoryService;

    public DeleteClientHandler(IClientRepositoryService clientRepositoryService) {
        this.clientRepositoryService = clientRepositoryService;
    }

    @Override
    public Void handle(DeleteClientCommand command) {
        // Supprimer le client par son ID
        this.clientRepositoryService.delete(command.getId());
        return null;
    }
}
