package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Classe;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.repository.g_Formation.ApprenantRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ClasseRepository;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.PlanFormationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ClasseRepositoryService {

    private final ClasseRepository classeRepository;
    private final FormationRepository formationRepository;
    private final PlanFormationRepository planFormationRepository;
    private final ApprenantRepository apprenantRepository;

    public ClasseRepositoryService(ClasseRepository classeRepository,
                                   FormationRepository formationRepository,
                                   PlanFormationRepository planFormationRepository,
                                   ApprenantRepository apprenantRepository) {
        this.classeRepository = classeRepository;
        this.formationRepository = formationRepository;
        this.planFormationRepository = planFormationRepository;
        this.apprenantRepository = apprenantRepository;
    }

    // ==================== CRUD Operations ====================

    public Classe saveClasse(Classe classe) {
        if (classe.getCode() == null || classe.getCode().isEmpty()) {
            classe.setCode(generateCode());
        }
        return classeRepository.save(classe);
    }

    public Classe createClasse(String nom, String description, Integer capaciteMax,
                               Long formationId, Long planFormationId, LocalDate dateDebutAcces, LocalDate dateFinAcces) {
        Classe classe = new Classe();
        classe.setNom(nom);
        classe.setDescription(description);
        classe.setCapaciteMax(capaciteMax);
        classe.setIsActive(true);
        classe.setCode(generateCode());
        classe.setDateDebutAcces(dateDebutAcces);
        classe.setDateFinAcces(dateFinAcces);

        // Set formation or plan formation
        if (planFormationId != null) {
            PlanFormation planFormation = planFormationRepository.findById(planFormationId)
                    .orElseThrow(() -> new RuntimeException("Plan formation non trouvé: " + planFormationId));
            classe.setPlanFormation(planFormation);
        } else if (formationId != null) {
            Formation formation = formationRepository.findById(formationId)
                    .orElseThrow(() -> new RuntimeException("Formation non trouvée: " + formationId));
            classe.setFormation(formation);
        }

        return classeRepository.save(classe);
    }

    public Classe updateClasse(Long id, String nom, String description, Integer capaciteMax,
                               Long formationId, Long planFormationId, Boolean isActive,
                               LocalDate dateDebutAcces, LocalDate dateFinAcces) {
        return classeRepository.findById(id)
                .map(classe -> {
                    if (nom != null) classe.setNom(nom);
                    if (description != null) classe.setDescription(description);
                    if (capaciteMax != null) classe.setCapaciteMax(capaciteMax);
                    if (isActive != null) classe.setIsActive(isActive);
                    classe.setDateDebutAcces(dateDebutAcces);
                    classe.setDateFinAcces(dateFinAcces);

                    // Update associations
                    if (planFormationId != null) {
                        PlanFormation planFormation = planFormationRepository.findById(planFormationId)
                                .orElseThrow(() -> new RuntimeException("Plan formation non trouvé: " + planFormationId));
                        classe.setPlanFormation(planFormation);
                        classe.setFormation(null); // Clear direct formation link
                    } else if (formationId != null) {
                        Formation formation = formationRepository.findById(formationId)
                                .orElseThrow(() -> new RuntimeException("Formation non trouvée: " + formationId));
                        classe.setFormation(formation);
                        classe.setPlanFormation(null); // Clear plan formation link
                    }

                    return classeRepository.save(classe);
                })
                .orElseThrow(() -> new RuntimeException("Classe non trouvée: " + id));
    }

    public void deleteClasse(Long id) {
        if (!classeRepository.existsById(id)) {
            throw new RuntimeException("Classe non trouvée: " + id);
        }
        classeRepository.deleteById(id);
    }

    public Optional<Classe> getClasseById(Long id) {
        return classeRepository.findByIdWithApprenants(id);
    }

    public List<Classe> getAllClasses() {
        return classeRepository.findAll();
    }

    // ==================== Search & Filter ====================

    public Page<Classe> searchClasses(String search, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return classeRepository.searchClasses(search, pageable);
    }

    public List<Classe> getClassesByFormation(Long formationId) {
        return classeRepository.findAllByFormationId(formationId);
    }

    public List<Classe> getClassesByPlanFormation(Long planFormationId) {
        return classeRepository.findByPlanFormation_IdPlanFormation(planFormationId);
    }

    public List<Classe> getActiveClasses() {
        return classeRepository.findByIsActiveTrue();
    }

    public List<Classe> getClassesWithAvailableSpots() {
        return classeRepository.findClassesWithAvailableSpots();
    }

    // ==================== Apprenant Management ====================

    public Classe addApprenantToClasse(Long classeId, Long apprenantId) {
        Classe classe = classeRepository.findByIdWithApprenants(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée: " + classeId));

        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé: " + apprenantId));

        // Check capacity
        if (classe.isFull()) {
            throw new RuntimeException("La classe a atteint sa capacité maximale");
        }

        // Check if already in class
        if (classe.getApprenants().contains(apprenant)) {
            throw new RuntimeException("L'apprenant est déjà inscrit dans cette classe");
        }

        classe.getApprenants().add(apprenant);
        return classeRepository.save(classe);
    }

    public Classe removeApprenantFromClasse(Long classeId, Long apprenantId) {
        Classe classe = classeRepository.findByIdWithApprenants(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée: " + classeId));

        classe.getApprenants().removeIf(a -> a.getId().equals(apprenantId));
        return classeRepository.save(classe);
    }

    public Classe addMultipleApprenantsToClasse(Long classeId, List<Long> apprenantIds) {
        Classe classe = classeRepository.findByIdWithApprenants(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée: " + classeId));

        for (Long apprenantId : apprenantIds) {
            Apprenant apprenant = apprenantRepository.findById(apprenantId).orElse(null);
            if (apprenant != null && !classe.getApprenants().contains(apprenant)) {
                if (!classe.isFull()) {
                    classe.getApprenants().add(apprenant);
                }
            }
        }

        return classeRepository.save(classe);
    }

    public List<Classe> getClassesByApprenant(Long apprenantId) {
        return classeRepository.findByApprenantId(apprenantId);
    }

    // ==================== Statistics ====================

    public long countTotalClasses() {
        return classeRepository.count();
    }

    public long countActiveClasses() {
        return classeRepository.countByIsActiveTrue();
    }

    public long countClassesByFormation(Long formationId) {
        return classeRepository.countByFormationId(formationId);
    }

    // ==================== Helpers ====================

    public Classe toggleActive(Long id) {
        return classeRepository.findById(id)
                .map(classe -> {
                    classe.setIsActive(!classe.getIsActive());
                    return classeRepository.save(classe);
                })
                .orElseThrow(() -> new RuntimeException("Classe non trouvée: " + id));
    }

    private String generateCode() {
        String prefix = "CLS";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp + random;
    }

    public boolean existsByCode(String code) {
        return classeRepository.existsByCode(code);
    }

    public Optional<Classe> findByCode(String code) {
        return classeRepository.findByCode(code);
    }
}
