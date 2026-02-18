package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.addContenuJourFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.springframework.stereotype.Component;

@Component("AddContenuJourFormationHandler")
public class AddContenuJourFormationHandler implements RequestHandler<AddContenuJourFormationCommand, AddContenuJourFormationResponse> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final PlanFormationRepositoryService planFormationRepositoryService;

    public AddContenuJourFormationHandler(
            ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
            ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
            PlanFormationRepositoryService planFormationRepositoryService
    ) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.planFormationRepositoryService = planFormationRepositoryService;
    }

    @Override
    public AddContenuJourFormationResponse handle(AddContenuJourFormationCommand command) {

        ContenuJourFormation contenuJourFormation = new ContenuJourFormation();
        contenuJourFormation.setContenu(command.getContenu());
        contenuJourFormation.setMoyenPedagogique(command.getMoyenPedagogique());
        contenuJourFormation.setSupportPedagogique(command.getSupportPedagogique());
        contenuJourFormation.setNbHeuresTheoriques(command.getNbHeuresTheoriques());
        contenuJourFormation.setNbHeuresPratiques(command.getNbHeuresPratiques());
        contenuJourFormation.setNumeroJour(command.getNumeroJour() != null ? command.getNumeroJour() : 1);
        contenuJourFormation.setStaff(command.getStaff());
        contenuJourFormation.setNiveau(command.getNiveau());

        ObjectifSpecifique objectif = objectifSpecifiqueRepositoryService
                .getObjectifSpecifiqueById(command.getIdObjectifSpecifique())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif spécifique non trouvé avec l'ID : " + command.getIdObjectifSpecifique()
                ));
        contenuJourFormation.setObjectifSpecifique(objectif);

        // idPlanFormation is optional
        if (command.getIdPlanFormation() != null) {
            PlanFormation planFormation = planFormationRepositoryService
                    .getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Plan formation non trouvé avec l'ID : " + command.getIdPlanFormation()
                    ));
            contenuJourFormation.setPlanFormation(planFormation);
        }

        ContenuJourFormation saved = contenuJourFormationRepositoryService
                .saveContenuJourFormation(contenuJourFormation);

        return new AddContenuJourFormationResponse(
                saved.getIdContenuJour(),
                "Contenu du jour ajouté avec succès."
        );
    }
}
