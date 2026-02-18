package com.example.BacK.application.g_Formation.Command.Formation.unlinkObjectifSpecifique;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifSpecifiqueRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnlinkObjectifSpecifiqueHandler implements RequestHandler<UnlinkObjectifSpecifiqueCommand, Void> {

    private final FormationRepository formationRepository;
    private final ObjectifSpecifiqueRepository objectifSpecifiqueRepository;

    public UnlinkObjectifSpecifiqueHandler(FormationRepository formationRepository,
                                          ObjectifSpecifiqueRepository objectifSpecifiqueRepository) {
        this.formationRepository = formationRepository;
        this.objectifSpecifiqueRepository = objectifSpecifiqueRepository;
    }

    @Override
    public Void handle(UnlinkObjectifSpecifiqueCommand command) {
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation not found: " + command.getFormationId()));

        ObjectifSpecifique objectifSpecifique = objectifSpecifiqueRepository.findById(command.getObjectifSpecifiqueId())
                .orElseThrow(() -> new RuntimeException("ObjectifSpecifique not found: " + command.getObjectifSpecifiqueId()));

        // Remove objectif specifique if linked
        formation.getObjectifsSpecifiques().remove(objectifSpecifique);
        formationRepository.save(formation);
        return null;
    }
}
