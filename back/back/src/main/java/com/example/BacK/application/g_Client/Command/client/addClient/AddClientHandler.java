package com.example.BacK.application.g_Client.Command.client.addClient;

import com.example.BacK.application.interfaces.g_Client.client.IClientRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Client.Client;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddClientHandler")
public class AddClientHandler implements RequestHandler<AddClientCommand, AddClientResponse> {

    private final IClientRepositoryService clientRepositoryService;
    private final ModelMapper modelMapper;

    public AddClientHandler(IClientRepositoryService clientRepositoryService, ModelMapper modelMapper) {
        this.clientRepositoryService = clientRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddClientResponse handle(AddClientCommand command) {
        Client client = modelMapper.map(command, Client.class);
        String id = this.clientRepositoryService.add(client);
        return new AddClientResponse(id);
    }
}

