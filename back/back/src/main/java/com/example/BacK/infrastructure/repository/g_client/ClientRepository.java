package com.example.BacK.infrastructure.repository.g_client;

import com.example.BacK.domain.g_Client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}

