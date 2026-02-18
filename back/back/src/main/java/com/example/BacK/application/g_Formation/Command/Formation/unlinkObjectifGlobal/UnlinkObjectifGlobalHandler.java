package com.example.BacK.application.g_Formation.Command.Formation.unlinkObjectifGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnlinkObjectifGlobalHandler implements RequestHandler<UnlinkObjectifGlobalCommand, Void> {

    private final FormationRepository formationRepository;
    private final ObjectifGlobalRepository objectifGlobalRepository;

    public UnlinkObjectifGlobalHandler(FormationRepository formationRepository,
                                      ObjectifGlobalRepository objectifGlobalRepository) {
        this.formationRepository = formationRepository;
        this.objectifGlobalRepository = objectifGlobalRepository;
    }

    @Override
    public Void handle(UnlinkObjectifGlobalCommand command) {
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation not found: " + command.getFormationId()));

        ObjectifGlobal objectifGlobal = objectifGlobalRepository.findById(command.getObjectifGlobalId())
                .orElseThrow(() -> new RuntimeException("ObjectifGlobal not found: " + command.getObjectifGlobalId()));

        // Remove objectif global if linked
        formation.getObjectifsGlobaux().remove(objectifGlobal);
        formationRepository.save(formation);
        return null;
    }
}
