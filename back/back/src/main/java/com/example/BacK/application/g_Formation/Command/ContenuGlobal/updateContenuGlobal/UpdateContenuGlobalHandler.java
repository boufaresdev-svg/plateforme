package com.example.BacK.application.g_Formation.Command.ContenuGlobal.updateContenuGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateContenuGlobalHandler")
public class UpdateContenuGlobalHandler implements RequestHandler<UpdateContenuGlobalCommand, Void> {

    private final ContenuGlobalRepositoryService contenuGlobalRepositoryService;
    private final FormationRepositoryService formationRepositoryService;

    public UpdateContenuGlobalHandler(ContenuGlobalRepositoryService contenuGlobalRepositoryService,
                                      FormationRepositoryService formationRepositoryService) {
        this.contenuGlobalRepositoryService = contenuGlobalRepositoryService;
        this.formationRepositoryService = formationRepositoryService;
    }

    @Override
    public Void handle(UpdateContenuGlobalCommand command) {
        if (command.getIdContenuGlobal() == null) {
            throw new IllegalArgumentException("L’ID du contenu global ne peut pas être nul.");
        }

        ContenuGlobal existing = contenuGlobalRepositoryService.getContenuGlobalById(command.getIdContenuGlobal())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun contenu global trouvé avec l’ID : " + command.getIdContenuGlobal()
                ));

        existing.setTitre(command.getTitre());
        existing.setDescription(command.getDescription());

        if (command.getIdFormation() != null) {
            Formation formation = formationRepositoryService.getFormationById(command.getIdFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucune formation trouvée avec l’ID : " + command.getIdFormation()
                    ));
            existing.setFormation(formation);
        }

        contenuGlobalRepositoryService.saveContenuGlobal(existing);

        return null;
    }
}
