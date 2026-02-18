package com.example.BacK.infrastructure.services.g_Client;

import com.example.BacK.application.g_Client.Query.client.GetClientResponse;
import com.example.BacK.application.interfaces.g_Client.client.IClientRepositoryService;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.infrastructure.repository.g_client.ClientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientRepositoryService implements IClientRepositoryService {

    private final ClientRepository clientRepository;
    private final ModelMapper mapper;

    public ClientRepositoryService(ClientRepository clientRepository, ModelMapper mapper) {
        this.clientRepository = clientRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Client client) {
        client.setId(null); // Assure-toi que l'ID est null pour création
        clientRepository.save(client);
        return "ok";
    }

    @Override
    public void update(Client client) {
        if (!clientRepository.existsById(client.getId())) {
            throw new IllegalArgumentException("Client ID not found");
        }
        clientRepository.save(client);
    }

    @Override
    public void delete(String id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Client getByid(String id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetClientResponse> getAll() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .map(c -> mapper.map(c, GetClientResponse.class))
                .toList();
    }


}
