package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.ContenuJourFormation.IContenuJourFormationRepositoryService;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuJourFormationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContenuJourFormationRepositoryService implements IContenuJourFormationRepositoryService {

    private final ContenuJourFormationRepository contenuJourFormationRepository;

    public ContenuJourFormationRepositoryService(ContenuJourFormationRepository contenuJourFormationRepository) {
        this.contenuJourFormationRepository = contenuJourFormationRepository;
    }

    @Override
    public ContenuJourFormation saveContenuJourFormation(ContenuJourFormation contenuJourFormation) {
        return contenuJourFormationRepository.save(contenuJourFormation);
    }

    @Override
    public ContenuJourFormation updateContenuJourFormation(Long id, ContenuJourFormation contenuJourFormation) {
        return contenuJourFormationRepository.findById(id)
                .map(existing -> {
                    existing.setContenu(contenuJourFormation.getContenu());
                    existing.setNbHeuresTheoriques(contenuJourFormation.getNbHeuresTheoriques());
                    existing.setNbHeuresPratiques(contenuJourFormation.getNbHeuresPratiques());
                    existing.setSupportPedagogique(contenuJourFormation.getSupportPedagogique());
                    existing.setObjectifSpecifique(contenuJourFormation.getObjectifSpecifique());
                    return contenuJourFormationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("ContenuJourFormation non trouvé avec l'ID : " + id));
    }

    @Override
    public void deleteContenuJourFormation(Long id) {
        if (!contenuJourFormationRepository.existsById(id)) {
            throw new RuntimeException("ContenuJourFormation non trouvé avec l'ID : " + id);
        }
        contenuJourFormationRepository.deleteById(id);
    }

    @Override
    public Optional<ContenuJourFormation> getContenuJourFormationById(Long id) {
        return contenuJourFormationRepository.findById(id);
    }

    @Override
    public List<ContenuJourFormation> getAllContenusJourFormation() {
        return contenuJourFormationRepository.findAll();
    }

    @Override
    public List<ContenuJourFormation> findByObjectif(Long idObjectifSpec) {
        return contenuJourFormationRepository.findByObjectifSpecifique_IdObjectifSpec(idObjectifSpec);
    }

    @Override
    public boolean existsById(Long id) {
        return contenuJourFormationRepository.existsById(id);
    }

    public Optional<ContenuJourFormation> getContenuJourFormationByIdWithContenusDetailles(Long id) {
        return contenuJourFormationRepository.findByIdWithContenusDetailles(id);
    }

    public List<ContenuJourFormation> getContenusByPlanFormationOrdered(Long planId) {
        return contenuJourFormationRepository.findByPlanFormationOrderByOrdre(planId);
    }

    public void updateContenuOrdre(Long contenuId, Integer ordre) {
        contenuJourFormationRepository.findById(contenuId)
                .ifPresent(contenu -> {
                    contenu.setOrdre(ordre);
                    contenuJourFormationRepository.save(contenu);
                });
    }
}
