package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.create;

import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifSpecifiqueRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateObjectifSpecifiqueHandler {

    private final ObjectifSpecifiqueRepository objectifSpecifiqueRepository;
    private final FormationRepository formationRepository;

    public CreateObjectifSpecifiqueHandler(ObjectifSpecifiqueRepository objectifSpecifiqueRepository,
                                          FormationRepository formationRepository) {
        this.objectifSpecifiqueRepository = objectifSpecifiqueRepository;
        this.formationRepository = formationRepository;
    }

    @Transactional
    public ObjectifSpecifique handle(CreateObjectifSpecifiqueCommand command) {
        // Validate that formationId is provided
        if (command.getFormationId() == null) {
            throw new IllegalArgumentException("formationId is required when creating an ObjectifSpecifique");
        }

        // Fetch the formation
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation not found: " + command.getFormationId()));

        // Create the objectif
        ObjectifSpecifique objectifSpecifique = new ObjectifSpecifique();
        objectifSpecifique.setTitre(command.getTitre());
        objectifSpecifique.setDescription(command.getDescription());
        objectifSpecifique.setTags(command.getTags());

        // Save the objectif first
        ObjectifSpecifique savedObjectif = objectifSpecifiqueRepository.save(objectifSpecifique);

        // Link to formation
        if (!formation.getObjectifsSpecifiques().contains(savedObjectif)) {
            formation.getObjectifsSpecifiques().add(savedObjectif);
            formationRepository.save(formation);
        }

        return savedObjectif;
    }
}
