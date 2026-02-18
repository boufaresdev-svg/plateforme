package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.ContenuDetaille.IContenuDetailleRepositoryService;
import com.example.BacK.domain.g_Formation.*;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuDetailleRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuJourFormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ContenuDetailleRepositoryService implements IContenuDetailleRepositoryService {

    private final ContenuDetailleRepository contenuDetailleRepository;
    private final ContenuJourFormationRepository contenuJourFormationRepository;
    private final FormationRepository formationRepository;

    public ContenuDetailleRepositoryService(
            ContenuDetailleRepository contenuDetailleRepository,
            ContenuJourFormationRepository contenuJourFormationRepository,
            FormationRepository formationRepository) {
        this.contenuDetailleRepository = contenuDetailleRepository;
        this.contenuJourFormationRepository = contenuJourFormationRepository;
        this.formationRepository = formationRepository;
    }

    @Override
    public ContenuDetaille saveContenuDetaille(ContenuDetaille contenuDetaille) {
        if (contenuDetaille.getIdContenuDetaille() != null &&
                !contenuDetailleRepository.existsById(contenuDetaille.getIdContenuDetaille())) {
            contenuDetaille.setIdContenuDetaille(null);
        }
        return contenuDetailleRepository.save(contenuDetaille);
    }

    @Override
    public ContenuDetaille updateContenuDetaille(Long id, ContenuDetaille contenuDetaille) {
        // The entity passed in has already been fetched and modified by the handler
        // Just save it directly - don't fetch again and lose the changes!
        if (!contenuDetailleRepository.existsById(id)) {
            throw new RuntimeException("ContenuDetaille non trouvé avec l'ID : " + id);
        }
        return contenuDetailleRepository.save(contenuDetaille);
    }

    @Override
    public void deleteContenuDetaille(Long id) {
        if (!contenuDetailleRepository.existsById(id)) {
            throw new RuntimeException("ContenuDetaille non trouvé avec l'ID : " + id);
        }
        contenuDetailleRepository.deleteById(id);
    }

    @Override
    public Optional<ContenuDetaille> getContenuDetailleById(Long id) {
        return contenuDetailleRepository.findById(id);
    }

    @Override
    public List<ContenuDetaille> getAllContenusDetailles() {
        return contenuDetailleRepository.findAll();
    }

    // Public method for external access
    public List<ContenuDetaille> getAllContenuDetaille() {
        return contenuDetailleRepository.findAll();
    }

    @Override
    public List<ContenuDetaille> getContenuDetailleByJour(Long idJour) {
        return contenuDetailleRepository.findByJourFormation_IdJour(idJour);
    }

    @Override
    public List<ContenuDetaille> getContenuDetailleByProgramme(Long idProgramme) {
        return contenuDetailleRepository.findByJourFormation_ProgrammeDetaile_IdProgramme(idProgramme);
    }

    @Override
    public List<ContenuDetaille> getContenuDetailleByFormation(Long idFormation) {
        // Get the formation with its objectifs
        Optional<Formation> formationOpt = formationRepository.findByIdWithObjectifs(idFormation);
        if (formationOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        Formation formation = formationOpt.get();
        
        // Get all ObjectifSpecifique IDs from this formation
        Set<Long> objectifSpecIds = new HashSet<>();
        if (formation.getObjectifsSpecifiques() != null) {
            formation.getObjectifsSpecifiques().forEach(os -> objectifSpecIds.add(os.getIdObjectifSpec()));
        }
        
        // If no objectifs, return empty
        if (objectifSpecIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Fetch all ContenuJourFormation IDs
        List<Long> allIds = contenuJourFormationRepository.findAllIds();
        
        // Collect unique ContenuDetaille IDs
        Set<Long> contenuDetailleIds = new HashSet<>();
        
        for (Long id : allIds) {
            Optional<ContenuJourFormation> cjfOpt = contenuJourFormationRepository.findByIdWithContenusDetailles(id);
            if (cjfOpt.isPresent()) {
                ContenuJourFormation cjf = cjfOpt.get();
                
                // Check if this ContenuJourFormation belongs to one of our objectifs
                if (cjf.getObjectifSpecifique() != null && 
                    objectifSpecIds.contains(cjf.getObjectifSpecifique().getIdObjectifSpec())) {
                    
                    // Extract ContenuDetaille IDs from assignments
                    if (cjf.getContenuAssignments() != null) {
                        for (ContenuJourNiveauAssignment assignment : cjf.getContenuAssignments()) {
                            ContenuDetaille cd = assignment.getContenuDetaille();
                            if (cd != null) {
                                contenuDetailleIds.add(cd.getIdContenuDetaille());
                            }
                        }
                    }
                }
            }
        }
        
        // Fetch actual ContenuDetaille entities by IDs to avoid proxy issues
        if (contenuDetailleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return contenuDetailleRepository.findAllById(contenuDetailleIds);
    }

    @Override
    public List<ContenuDetaille> getContenuDetailleByTitre(String titre) {
        return contenuDetailleRepository.findByTitre(titre);
    }

    public List<ContenuDetaille> searchContenuDetaille(String query) {
        if (query == null || query.trim().isEmpty()) {
            return contenuDetailleRepository.findAll();
        }
        return contenuDetailleRepository.findByTitreContainingIgnoreCase(query.trim());
    }

    @Override
    public boolean existsById(Long id) {
        return contenuDetailleRepository.existsById(id);
    }
}
