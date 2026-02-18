package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Examen.IExamenRepositoryService;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.repository.g_Formation.ApprenantRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ExamenRepository;
import com.example.BacK.infrastructure.repository.g_Formation.PlanFormationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamenRepositoryService implements IExamenRepositoryService {

    private final ExamenRepository examenRepository;
    private final ApprenantRepository apprenantRepository;
    private final PlanFormationRepository planFormationRepository;

    public ExamenRepositoryService(
            ExamenRepository examenRepository,
            ApprenantRepository apprenantRepository,
            PlanFormationRepository planFormationRepository
    ) {
        this.examenRepository = examenRepository;
        this.apprenantRepository = apprenantRepository;
        this.planFormationRepository = planFormationRepository;
    }

    @Override
    public Examen saveExamen(Examen examen) {
        return examenRepository.save(examen);
    }

    @Override
    public Examen updateExamen(Long id, Examen examen) {

        return examenRepository.findById(id)
                .map(existing -> {

                    existing.setType(examen.getType());
                    existing.setDate(examen.getDate());
                    existing.setDescription(examen.getDescription());
                    existing.setScore(examen.getScore());

                    if (examen.getApprenant().getId() != null) {
                        Apprenant apprenant = apprenantRepository.findById(examen.getApprenant().getId())
                                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé : " + examen.getApprenant().getId()));
                        existing.setApprenant(apprenant);
                    }

                    if (examen.getPlanFormation().getIdPlanFormation() != null) {
                        PlanFormation plan = planFormationRepository.findById(examen.getPlanFormation().getIdPlanFormation())
                                .orElseThrow(() -> new RuntimeException("Plan formation non trouvé : " + examen.getPlanFormation().getIdPlanFormation()));
                        existing.setPlanFormation(plan);
                    }

                    existing.setCertificat(examen.getCertificat());

                    return examenRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Examen non trouvé avec l'ID : " + id));
    }

    @Override
    public void deleteExamen(Long id) {
        if (!examenRepository.existsById(id)) {
            throw new RuntimeException("Examen non trouvé avec l'ID : " + id);
        }
        examenRepository.deleteById(id);
    }

    @Override
    public Optional<Examen> getExamenById(Long id) {
        return examenRepository.findById(id);
    }

    @Override
    public List<Examen> getAllExamens() {
        return examenRepository.findAll();
    }

    @Override
    public List<Examen> getExamensByPlanFormation(Long idPlanFormation) {
        return examenRepository.findByApprenant_PlanFormation_IdPlanFormation(idPlanFormation);
    }

    @Override
    public boolean existsById(Long id) {
        return examenRepository.existsById(id);
    }
}

