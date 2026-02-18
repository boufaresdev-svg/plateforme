package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.JourFormation.IJourFormationRepositoryService;
import com.example.BacK.domain.g_Formation.JourFormation;
import com.example.BacK.infrastructure.repository.g_Formation.JourFormationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JourFormationRepositoryService implements IJourFormationRepositoryService {

    private final JourFormationRepository jourFormationRepository;

    public JourFormationRepositoryService(JourFormationRepository jourFormationRepository) {
        this.jourFormationRepository = jourFormationRepository;
    }

    @Override
    public Optional<JourFormation> getJourFormationById(Long id) {
        return jourFormationRepository.findById(id);
    }
}
