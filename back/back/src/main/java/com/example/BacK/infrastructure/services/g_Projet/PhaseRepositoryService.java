package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.phase.all.GetPhaseResponse;
import com.example.BacK.application.interfaces.g_Projet.phase.IPhaseRespositoryService;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.infrastructure.repository.g_Projet.PhaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhaseRepositoryService implements IPhaseRespositoryService {

    private final PhaseRepository phaseRepository;
    private final ModelMapper mapper;

    public PhaseRepositoryService(PhaseRepository phaseRepository, ModelMapper mapper) {
        this.phaseRepository = phaseRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Phase phase) {
        phase.setId(null); // ID null pour création
        phaseRepository.save(phase);
        return "ok";
    }

    @Override
    public void update(Phase phase) {
        if (!phaseRepository.existsById(phase.getId())) {
            throw new IllegalArgumentException("Phase ID not found");
        }
        phaseRepository.save(phase);
    }

    @Override
    public void delete(String id) {
        phaseRepository.deleteById(id);
    }

    @Override
    public Phase get(String id) {
        return phaseRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetPhaseResponse> getall() {
        List<Phase> phases = phaseRepository.findAll();
        return phases.stream()
                .map(p -> mapper.map(p, GetPhaseResponse.class))
                .toList();
    }
}
