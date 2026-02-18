package com.example.BacK.application.interfaces.g_Formation.JourFormation;

import com.example.BacK.domain.g_Formation.JourFormation;

import java.util.Optional;

public interface IJourFormationRepositoryService {
    Optional<JourFormation> getJourFormationById(Long id);
}
