package com.example.BacK.application.g_Formation.Command.ObjectifGlobal.create;

import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateObjectifGlobalHandler {

    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final FormationRepository formationRepository;

    public CreateObjectifGlobalHandler(ObjectifGlobalRepository objectifGlobalRepository,
                                      FormationRepository formationRepository) {
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.formationRepository = formationRepository;
    }

    @Transactional
    public ObjectifGlobal handle(CreateObjectifGlobalCommand command) {
        // Validate that formationId is provided
        if (command.getFormationId() == null) {
            throw new IllegalArgumentException("formationId is required when creating an ObjectifGlobal");
        }

        // Fetch the formation
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation not found: " + command.getFormationId()));

        // Create the objectif
        ObjectifGlobal objectifGlobal = new ObjectifGlobal();
        objectifGlobal.setLibelle(command.getLibelle());
        objectifGlobal.setDescription(command.getDescription());
        objectifGlobal.setTags(command.getTags());

        // Save the objectif first
        ObjectifGlobal savedObjectif = objectifGlobalRepository.save(objectifGlobal);

        // Link to formation
        if (!formation.getObjectifsGlobaux().contains(savedObjectif)) {
            formation.getObjectifsGlobaux().add(savedObjectif);
            formationRepository.save(formation);
        }

        return savedObjectif;
    }
}
