package com.example.BacK.application.interfaces.g_Formation.PlanFormation;

import com.example.BacK.domain.g_Formation.PlanFormation;

import java.util.List;
import java.util.Optional;

public interface IPlanFormationRepositoryService {

    PlanFormation savePlanFormation(PlanFormation planFormation);
    void updatePlanFormation(Long id, PlanFormation planFormation);
    void deletePlanFormation(Long id);
    Optional<PlanFormation> getPlanFormationById(Long id);
    List<PlanFormation> getAllPlanFormations();
    List<PlanFormation> findByFormationId(Long formationId);
    boolean existsById(Long id);
}
