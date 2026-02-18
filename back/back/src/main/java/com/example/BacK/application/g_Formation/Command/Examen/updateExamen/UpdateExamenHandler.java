package com.example.BacK.application.g_Formation.Command.Examen.updateExamen;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("UpdateExamenHandler")
public class UpdateExamenHandler implements RequestHandler<UpdateExamenCommand, Void> {

    private final ExamenRepositoryService examenRepo;
    private final ApprenantRepositoryService apprenantRepo;
    private final PlanFormationRepositoryService planFormationRepo;

    public UpdateExamenHandler(
            ExamenRepositoryService examenRepo,
            ApprenantRepositoryService apprenantRepo,
            PlanFormationRepositoryService planFormationRepo
    ) {
        this.examenRepo = examenRepo;
        this.apprenantRepo = apprenantRepo;
        this.planFormationRepo = planFormationRepo;
    }

    @Override
    public Void handle(UpdateExamenCommand command) {

        Examen ex = examenRepo.getExamenById(command.getIdExamen())
                .orElseThrow(() -> new IllegalArgumentException("Examen introuvable"));

        ex.setType(command.getType());
        ex.setDescription(command.getDescription());
        ex.setScore(command.getScore());
        ex.setDate(command.getDate());

        if (command.getIdApprenant() != null) {
            Apprenant ap = apprenantRepo.getApprenantById(command.getIdApprenant())
                    .orElseThrow(() -> new IllegalArgumentException("Apprenant introuvable"));
            ex.setApprenant(ap);
        }

        if (command.getIdPlanFormation() != null) {
            PlanFormation pf = planFormationRepo.getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException("Plan formation introuvable"));
            ex.setPlanFormation(pf);
        }

        examenRepo.saveExamen(ex);
        return null;
    }
}
