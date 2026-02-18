package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.ObjectifSpecifique.IObjectifSpecifiqueRepositoryService;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifSpecifiqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectifSpecifiqueRepositoryService implements IObjectifSpecifiqueRepositoryService {

    private final ObjectifSpecifiqueRepository objectifSpecifiqueRepository;

    public ObjectifSpecifiqueRepositoryService(ObjectifSpecifiqueRepository objectifSpecifiqueRepository) {
        this.objectifSpecifiqueRepository = objectifSpecifiqueRepository;
    }

    @Override
    public ObjectifSpecifique saveObjectifSpecifique(ObjectifSpecifique objectifSpecifique) {
        return objectifSpecifiqueRepository.saveAndFlush(objectifSpecifique);
    }

    @Override
    public ObjectifSpecifique updateObjectifSpecifique(Long id, ObjectifSpecifique objectifSpecifique) {
        return objectifSpecifiqueRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(objectifSpecifique.getTitre());
                    existing.setDescription(objectifSpecifique.getDescription());
                    existing.setContenuGlobal(objectifSpecifique.getContenuGlobal());
                    existing.setContenusJourFormation(objectifSpecifique.getContenusJourFormation());
                    return objectifSpecifiqueRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Objectif spécifique non trouvé avec l'ID : " + id));
    }

    @Override
    public void deleteObjectifSpecifique(Long id) {
        if (!objectifSpecifiqueRepository.existsById(id)) {
            throw new RuntimeException("Objectif spécifique non trouvé avec l'ID : " + id);
        }
        objectifSpecifiqueRepository.deleteById(id);
    }

    @Override
    public Optional<ObjectifSpecifique> getObjectifSpecifiqueById(Long id) {
        return objectifSpecifiqueRepository.findById(id);
    }

    @Override
    public List<ObjectifSpecifique> getAllObjectifsSpecifiques() {
        return objectifSpecifiqueRepository.findAll();
    }

    @Override
    public List<ObjectifSpecifique> getObjectifsByContenu(Long idContenu) {
        return objectifSpecifiqueRepository.findByContenuGlobal_IdContenuGlobal(idContenu);
    }

    @Override
    public boolean existsById(Long id) {
        return objectifSpecifiqueRepository.existsById(id);
    }
}
