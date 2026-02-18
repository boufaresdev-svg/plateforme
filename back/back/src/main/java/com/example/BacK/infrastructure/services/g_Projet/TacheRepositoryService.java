package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.Tache.all.GetTacheResponse;
import com.example.BacK.application.interfaces.g_Projet.tache.ITacheRepositoryService;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.infrastructure.repository.g_Projet.TacheRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TacheRepositoryService implements ITacheRepositoryService {

    private final TacheRepository tacheRepository;
    private final ModelMapper mapper;

    public TacheRepositoryService(TacheRepository tacheRepository, ModelMapper mapper) {
        this.tacheRepository = tacheRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Tache tache) {
        tache.setId(null); // ID null pour création
        tacheRepository.save(tache);
        return "ok";
    }

    @Override
    public void update(Tache tache) {
        if (!tacheRepository.existsById(tache.getId())) {
            throw new IllegalArgumentException("Tache ID not found");
        }
        tacheRepository.save(tache);
    }

    @Override
    public void delete(String id) {
        tacheRepository.deleteById(id);
    }

    @Override
    public Tache get(String id) {
        return tacheRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetTacheResponse> getall() {
        List<Tache> taches = tacheRepository.findAll();
        return taches.stream()
                .map(t -> mapper.map(t, GetTacheResponse.class))
                .toList();
    }
}
