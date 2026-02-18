package com.example.BacK.application.g_Formation.Command.ContenuDetaille.updateContenuDetaille;

import org.springframework.stereotype.Component;

/**
 * Validator for UpdateContenuDetailleCommand
 */
@Component
public class UpdateContenuDetailleValidator {

    public void validate(UpdateContenuDetailleCommand command) throws IllegalArgumentException {
        
        if (command.getIdContenuDetaille() == null || command.getIdContenuDetaille() <= 0) {
            throw new IllegalArgumentException("IdContenuDetaille is required");
        }
        
        if (command.getTitre() != null && command.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Titre cannot be empty");
        }
        
        if (command.getDureeTheorique() != null && command.getDureeTheorique() < 0) {
            throw new IllegalArgumentException("DureeTheorique cannot be negative");
        }
        
        if (command.getDureePratique() != null && command.getDureePratique() < 0) {
            throw new IllegalArgumentException("DureePratique cannot be negative");
        }
        
        if (command.getLevels() != null && !command.getLevels().isEmpty()) {
            for (var level : command.getLevels()) {
                if (level.getLevelNumber() < 1 || level.getLevelNumber() > 5) {
                    throw new IllegalArgumentException("Level number must be between 1 and 5");
                }
            }
        }
    }
}
