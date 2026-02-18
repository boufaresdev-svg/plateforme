package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.PlanFormation.IPlanFormationRepositoryService;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.repository.g_Formation.PlanFormationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanFormationRepositoryService implements IPlanFormationRepositoryService {

    private final PlanFormationRepository planFormationRepository;

    public PlanFormationRepositoryService(PlanFormationRepository planFormationRepository) {
        this.planFormationRepository = planFormationRepository;
    }


    @Override
    public PlanFormation savePlanFormation(PlanFormation planFormation) {
        return planFormationRepository.save(planFormation);
    }


    @Override
    public void updatePlanFormation(Long id, PlanFormation planFormation) {
        planFormationRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(planFormation.getTitre());
                    existing.setDescription(planFormation.getDescription());
                    existing.setDateLancement(planFormation.getDateLancement());
                    existing.setDateDebut(planFormation.getDateDebut());
                    existing.setDateFin(planFormation.getDateFin());
                    existing.setDateFinReel(planFormation.getDateFinReel());
                    existing.setStatusFormation(planFormation.getStatusFormation());
                    existing.setFormation(planFormation.getFormation());
                    existing.setFormateur(planFormation.getFormateur());
                    return planFormationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Plan de formation non trouvé avec l’ID : " + id));
    }


    @Override
    public void deletePlanFormation(Long id) {
        if (!planFormationRepository.existsById(id)) {
            throw new RuntimeException("Plan de formation non trouvé avec l’ID : " + id);
        }
        planFormationRepository.deleteById(id);
    }


    @Override
    public Optional<PlanFormation> getPlanFormationById(Long id) {
        return planFormationRepository.findById(id);
    }


    @Override
    public List<PlanFormation> getAllPlanFormations() {
        return planFormationRepository.findAll();
    }

    @Override
    public List<PlanFormation> findByFormationId(Long formationId) {
        return planFormationRepository.findByFormation_IdFormation(formationId);
    }


    @Override
    public boolean existsById(Long id) {
        return planFormationRepository.existsById(id);
    }
}
