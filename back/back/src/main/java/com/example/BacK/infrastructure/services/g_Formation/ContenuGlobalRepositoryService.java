package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.ContenuGlobal.IContenuGlobalRepositoryService;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuGlobalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContenuGlobalRepositoryService implements IContenuGlobalRepositoryService {

    private final ContenuGlobalRepository contenuGlobalRepository;

    public ContenuGlobalRepositoryService(ContenuGlobalRepository contenuGlobalRepository) {
        this.contenuGlobalRepository = contenuGlobalRepository;
    }

    @Override
    public ContenuGlobal saveContenuGlobal(ContenuGlobal contenuGlobal) {
        if (contenuGlobal.getIdContenuGlobal() != null &&
                !contenuGlobalRepository.existsById(contenuGlobal.getIdContenuGlobal())) {
            contenuGlobal.setIdContenuGlobal(null);
        }
        return contenuGlobalRepository.save(contenuGlobal);
    }

    @Override
    public ContenuGlobal updateContenuGlobal(Long id, ContenuGlobal contenuGlobal) {
        return contenuGlobalRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(contenuGlobal.getTitre());
                    existing.setDescription(contenuGlobal.getDescription());
                    existing.setFormation(contenuGlobal.getFormation());
                    existing.setObjectifsSpecifiques(contenuGlobal.getObjectifsSpecifiques());
                    return contenuGlobalRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("ContenuGlobal non trouvé avec l’ID : " + id));
    }

    @Override
    public void deleteContenuGlobal(Long id) {
        if (!contenuGlobalRepository.existsById(id)) {
            throw new RuntimeException("ContenuGlobal non trouvé avec l’ID : " + id);
        }
        contenuGlobalRepository.deleteById(id);
    }

    @Override
    public Optional<ContenuGlobal> getContenuGlobalById(Long id) {
        return contenuGlobalRepository.findById(id);
    }

    @Override
    public List<ContenuGlobal> getAllContenusGlobaux() {
        return contenuGlobalRepository.findAll();
    }

    @Override
    public List<ContenuGlobal> getContenusByFormation(Long idFormation) {
        return contenuGlobalRepository.findByFormation_IdFormation(idFormation);
    }

    @Override
    public boolean existsById(Long id) {
        return contenuGlobalRepository.existsById(id);
    }
}

