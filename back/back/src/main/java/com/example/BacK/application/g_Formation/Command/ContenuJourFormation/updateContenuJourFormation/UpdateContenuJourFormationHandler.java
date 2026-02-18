package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.updateContenuJourFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateContenuJourFormationHandler")
public class UpdateContenuJourFormationHandler implements RequestHandler<UpdateContenuJourFormationCommand, Void> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final PlanFormationRepositoryService planFormationRepositoryService;

    public UpdateContenuJourFormationHandler(
            ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
            ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
            PlanFormationRepositoryService planFormationRepositoryService) {

        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.planFormationRepositoryService = planFormationRepositoryService;
    }

    @Override
    public Void handle(UpdateContenuJourFormationCommand command) {

        ContenuJourFormation existing = contenuJourFormationRepositoryService
                .getContenuJourFormationById(command.getIdContenuJour())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun contenu jour trouvé avec l'ID : " + command.getIdContenuJour()
                ));

        existing.setContenu(command.getContenu());
        existing.setMoyenPedagogique(command.getMoyenPedagogique());
        existing.setSupportPedagogique(command.getSupportPedagogique());
        existing.setNbHeuresTheoriques(command.getNbHeuresTheoriques());
        existing.setNbHeuresPratiques(command.getNbHeuresPratiques());
        existing.setNumeroJour(command.getNumeroJour() != null ? command.getNumeroJour() : existing.getNumeroJour());
        existing.setStaff(command.getStaff());
        existing.setNiveau(command.getNiveau());

        if (command.getIdObjectifSpecifique() != null) {
            ObjectifSpecifique objectif = objectifSpecifiqueRepositoryService
                    .getObjectifSpecifiqueById(command.getIdObjectifSpecifique())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Objectif spécifique introuvable avec l'ID : " + command.getIdObjectifSpecifique()
                    ));

            existing.setObjectifSpecifique(objectif);
        }

        // idPlanFormation is optional
        if (command.getIdPlanFormation() != null) {
            PlanFormation plan = planFormationRepositoryService
                    .getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Plan de formation introuvable avec l'ID : " + command.getIdPlanFormation()
                    ));

            existing.setPlanFormation(plan);
        }

        contenuJourFormationRepositoryService.saveContenuJourFormation(existing);

        return null;
    }
}
