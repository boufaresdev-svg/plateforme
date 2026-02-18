package com.example.BacK.application.g_Formation.Command.ContenuDetaille.updateContenuDetaille;

import com.example.BacK.application.g_Formation.Command.ContenuDetaille.ContentLevelDto;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContentLevel;
import com.example.BacK.domain.g_Formation.JourFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.JourFormationRepositoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("UpdateContenuDetailleHandler")
public class UpdateContenuDetailleHandler implements RequestHandler<UpdateContenuDetailleCommand, UpdateContenuDetailleResponse> {

    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;
    private final JourFormationRepositoryService jourFormationRepositoryService;

    public UpdateContenuDetailleHandler(
            ContenuDetailleRepositoryService contenuDetailleRepositoryService,
            JourFormationRepositoryService jourFormationRepositoryService) {
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
        this.jourFormationRepositoryService = jourFormationRepositoryService;
    }

    @Override
    public UpdateContenuDetailleResponse handle(UpdateContenuDetailleCommand command) {
        // Validate contenu exists
        ContenuDetaille existing = contenuDetailleRepositoryService
                .getContenuDetailleById(command.getIdContenuDetaille())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ContenuDetaille non trouvé avec l'ID : " + command.getIdContenuDetaille()
                ));

        // Update jour if provided
        if (command.getIdJourFormation() != null) {
            JourFormation jourFormation = jourFormationRepositoryService
                    .getJourFormationById(command.getIdJourFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "JourFormation non trouvé avec l'ID : " + command.getIdJourFormation()
                    ));
            existing.setJourFormation(jourFormation);
        }

        // Update fields
        if (command.getTitre() != null) {
            existing.setTitre(command.getTitre());
        }
        if (command.getContenusCles() != null) {
            existing.setContenusCles(command.getContenusCles());
        }
        if (command.getMethodesPedagogiques() != null) {
            existing.setMethodesPedagogiques(command.getMethodesPedagogiques());
        }
        if (command.getDureeTheorique() != null) {
            existing.setDureeTheorique(command.getDureeTheorique());
        }
        if (command.getDureePratique() != null) {
            existing.setDureePratique(command.getDureePratique());
        }
        if (command.getTags() != null) {
            existing.setTags(command.getTags());
        }
        
        // Update levels - convert DTOs to entities and preserve file metadata
        if (command.getLevels() != null && !command.getLevels().isEmpty()) {
            // Step 1: Build a map of existing levels with their files
            Map<Integer, ContentLevel> existingLevelsMap = new HashMap<>();
            for (ContentLevel existingLevel : existing.getLevels()) {
                if (existingLevel.getLevelNumber() != null) {
                    ContentLevel current = existingLevelsMap.get(existingLevel.getLevelNumber());
                    // Keep this level if: no existing, OR this one has files and existing doesn't
                    if (current == null || 
                        (existingLevel.getFiles() != null && !existingLevel.getFiles().isEmpty() && 
                         (current.getFiles() == null || current.getFiles().isEmpty()))) {
                        existingLevelsMap.put(existingLevel.getLevelNumber(), existingLevel);
                    }
                }
            }
            
            // Step 2: Clear all levels to remove duplicates
            existing.getLevels().clear();
            
            // Step 3: Convert DTOs to entities and add updated levels with preserved files
            for (ContentLevelDto dto : command.getLevels()) {
                if (dto == null || dto.getLevelNumber() == null) {
                    continue;
                }
                
                // Create new entity from DTO
                ContentLevel newLevel = new ContentLevel();
                newLevel.setLevelNumber(dto.getLevelNumber());
                newLevel.setTheorieContent(dto.getTheorieContent());
                newLevel.setDureeTheorique(dto.getDureeTheorique());
                newLevel.setDureePratique(dto.getDureePratique());
                
                // Get existing level files (if any)
                ContentLevel oldLevel = existingLevelsMap.get(dto.getLevelNumber());
                if (oldLevel != null && oldLevel.getFiles() != null && !oldLevel.getFiles().isEmpty()) {
                    // Preserve existing files
                    newLevel.setFiles(new ArrayList<>(oldLevel.getFiles()));
                }
                
                // Set the parent reference for bidirectional relationship
                newLevel.setContenuDetaille(existing);
                existing.getLevels().add(newLevel);
            }
        }

        // Save and return response
        ContenuDetaille updated = contenuDetailleRepositoryService.updateContenuDetaille(
                command.getIdContenuDetaille(), existing);

        return new UpdateContenuDetailleResponse(
                updated.getIdContenuDetaille(),
                updated.getTitre(),
                "ContenuDetaille mis à jour avec succès !"
        );
    }
}
