package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.copyAndLink;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.springframework.stereotype.Component;

@Component("CopyAndLinkObjectifSpecifiqueHandler")
public class CopyAndLinkObjectifSpecifiqueHandler implements RequestHandler<CopyAndLinkObjectifSpecifiqueCommand, Void> {

    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final FormationRepository formationRepository;

    public CopyAndLinkObjectifSpecifiqueHandler(ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
                                               ObjectifGlobalRepository objectifGlobalRepository,
                                               FormationRepository formationRepository) {
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.formationRepository = formationRepository;
    }

    @Override
    public Void handle(CopyAndLinkObjectifSpecifiqueCommand command) {
        // Fetch the formation
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formation non trouvée avec l'ID : " + command.getFormationId()));
        
        // Fetch the existing objective to copy
        ObjectifSpecifique sourceObjectif = objectifSpecifiqueRepositoryService
                .getObjectifSpecifiqueById(command.getObjectifSpecifiqueId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif spécifique non trouvé avec l'ID : " + command.getObjectifSpecifiqueId()));

        // Fetch the global objective to link to
        ObjectifGlobal targetGlobal = objectifGlobalRepository.findById(command.getObjectifGlobalId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif global non trouvé avec l'ID : " + command.getObjectifGlobalId()));

        // Create a copy of the source objective
        ObjectifSpecifique copiedObjectif = new ObjectifSpecifique();
        copiedObjectif.setTitre(sourceObjectif.getTitre());
        copiedObjectif.setDescription(sourceObjectif.getDescription());
        copiedObjectif.setTags(sourceObjectif.getTags());
        
        // Link the copied objective to the target global objective
        copiedObjectif.setObjectifGlobal(targetGlobal);
        
        // Link to the same content global if exists
        if (sourceObjectif.getContenuGlobal() != null) {
            copiedObjectif.setContenuGlobal(sourceObjectif.getContenuGlobal());
        }

        // Save the copied objective first
        ObjectifSpecifique saved = objectifSpecifiqueRepositoryService.saveObjectifSpecifique(copiedObjectif);
        
        // Add the copied objective to the formation
        formation.getObjectifsSpecifiques().add(saved);
        formationRepository.saveAndFlush(formation);

        return null;
    }
}
