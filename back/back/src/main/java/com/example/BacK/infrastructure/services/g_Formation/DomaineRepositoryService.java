package com.example.BacK.infrastructure.services.g_Formation;


import com.example.BacK.application.interfaces.g_Formation.Domaine.IDomaineRepositoryService;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.infrastructure.repository.g_Formation.DomaineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DomaineRepositoryService implements IDomaineRepositoryService {

    private final DomaineRepository domaineRepository;

    public DomaineRepositoryService(DomaineRepository domaineRepository) {
        this.domaineRepository = domaineRepository;
    }


    @Override
    public Domaine saveDomaine(Domaine domaine) {
        return domaineRepository.save(domaine);
    }


    @Override
    public Domaine updateDomaine(Long id, Domaine domaine) {
        if (!domaineRepository.existsById(id)) {
            throw new IllegalArgumentException("Domaine non trouvé avec l'ID : " + id);
        }

        domaine.setIdDomaine(id);

        return domaineRepository.save(domaine);
    }


    @Override
    public void deleteDomaine(Long id) {
        if (!domaineRepository.existsById(id)) {
            throw new IllegalArgumentException("Domaine non trouvé avec l'ID : " + id);
        }

        domaineRepository.deleteById(id);
    }


    @Override
    public Optional<Domaine> getDomaineById(Long id) {
        return domaineRepository.findById(id);
    }


    @Override
    public List<Domaine> getAllDomaines() {
        return domaineRepository.findAll();
    }
}

