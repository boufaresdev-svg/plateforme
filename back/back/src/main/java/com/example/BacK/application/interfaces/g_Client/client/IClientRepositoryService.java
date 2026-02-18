package com.example.BacK.application.interfaces.g_Client.client;

import com.example.BacK.application.g_Client.Query.client.GetClientResponse;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Formation.Certificat;

import java.util.List;



public interface IClientRepositoryService {

    String add(Client client  );
    void update(Client client );
    void delete(String id);
    Client getByid(String id);
    List<GetClientResponse> getAll();

}
