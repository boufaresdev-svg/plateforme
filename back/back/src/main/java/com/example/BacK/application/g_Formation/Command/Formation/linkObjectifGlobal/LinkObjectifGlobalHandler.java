package com.example.BacK.application.g_Formation.Command.Formation.linkObjectifGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import org.springframework.stereotype.Component;

@Component
public class LinkObjectifGlobalHandler implements RequestHandler<LinkObjectifGlobalCommand, Void> {

    private final FormationRepository formationRepository;
    private final ObjectifGlobalRepository objectifGlobalRepository;

    public LinkObjectifGlobalHandler(FormationRepository formationRepository,
                                    ObjectifGlobalRepository objectifGlobalRepository) {
        this.formationRepository = formationRepository;
        this.objectifGlobalRepository = objectifGlobalRepository;
    }

    @Override
    public Void handle(LinkObjectifGlobalCommand command) {
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation not found: " + command.getFormationId()));

        ObjectifGlobal objectifGlobal = objectifGlobalRepository.findById(command.getObjectifGlobalId())
                .orElseThrow(() -> new RuntimeException("ObjectifGlobal not found: " + command.getObjectifGlobalId()));
        
        // Check if already linked by ID to avoid duplicates
        boolean alreadyLinked = formation.getObjectifsGlobaux().stream()
                .anyMatch(og -> og.getIdObjectifGlobal().equals(objectifGlobal.getIdObjectifGlobal()));
        
        if (!alreadyLinked) {
            // Add to formation side (owning side) - same pattern as CreateObjectifGlobalHandler
            formation.getObjectifsGlobaux().add(objectifGlobal);
            formationRepository.saveAndFlush(formation);
        }
        return null;
    }
}
