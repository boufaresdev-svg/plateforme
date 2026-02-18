package com.example.BacK.application.g_Client.Query.client;

import com.example.BacK.application.interfaces.g_Client.client.IClientRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Client.Client;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GetClientHandler")
public class GetClientHandler implements RequestHandler<GetClientQuery, List<GetClientResponse>> {

    private final IClientRepositoryService clientRepositoryService;
    private final ModelMapper modelMapper;

    public GetClientHandler(IClientRepositoryService clientRepositoryService, ModelMapper modelMapper) {
        this.clientRepositoryService = clientRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetClientResponse> handle(GetClientQuery query) {
         Client filter = modelMapper.map(query, Client.class);
         return clientRepositoryService.getAll();

    }
}
