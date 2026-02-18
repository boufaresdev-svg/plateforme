package com.example.BacK.application.g_Formation.Command.ContenuDetaille.addContenuDetaille;

import org.springframework.stereotype.Component;

/**
 * Validator for AddContenuDetailleCommand
 */
@Component
public class AddContenuDetailleValidator {

    public void validate(AddContenuDetailleCommand command) throws IllegalArgumentException {
        
        if (command.getTitre() == null || command.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Titre is required");
        }
        
        // idJourFormation is now optional - can be assigned later via drag-and-drop
        if (command.getIdJourFormation() != null && command.getIdJourFormation() <= 0) {
            throw new IllegalArgumentException("IdJourFormation must be positive when provided");
        }
        
        if (command.getDureeTheorique() != null && command.getDureeTheorique() < 0) {
            throw new IllegalArgumentException("DureeTheorique cannot be negative");
        }
        
        if (command.getDureePratique() != null && command.getDureePratique() < 0) {
            throw new IllegalArgumentException("DureePratique cannot be negative");
        }
        
        if (command.getLevels() != null && !command.getLevels().isEmpty()) {
            // Validate levels are between 1-5
            for (var level : command.getLevels()) {
                if (level.getLevelNumber() < 1 || level.getLevelNumber() > 5) {
                    throw new IllegalArgumentException("Level number must be between 1 and 5");
                }
            }
        }
    }
}
