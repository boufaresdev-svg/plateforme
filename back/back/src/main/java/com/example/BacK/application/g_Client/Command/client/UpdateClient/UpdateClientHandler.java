package com.example.BacK.application.g_Client.Command.client.UpdateClient;

import com.example.BacK.application.interfaces.g_Client.client.IClientRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Client.Client;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateClientHandler")
public class UpdateClientHandler implements RequestHandler<UpdateClientCommand, Void> {

    private final IClientRepositoryService clientRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateClientHandler(IClientRepositoryService clientRepositoryService, ModelMapper modelMapper) {
        this.clientRepositoryService = clientRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateClientCommand command) {
        // Récupérer le client existant
        Client existingClient = this.clientRepositoryService.getByid(command.getId());
        if (existingClient == null) {
            throw new EntityNotFoundException("Client not found");
        }

        // Mapper les nouvelles valeurs depuis le command
        this.modelMapper.map(command, existingClient);

        // Mettre à jour le client
        this.clientRepositoryService.update(existingClient);
        return null;
    }
}
