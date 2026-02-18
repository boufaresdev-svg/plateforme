package com.example.BacK.application.g_Formation.Command.ContenuDetaille.deleteContenuDetaille;

import org.springframework.stereotype.Component;

/**
 * Validator for DeleteContenuDetailleCommand
 */
@Component
public class DeleteContenuDetailleValidator {

    public void validate(DeleteContenuDetailleCommand command) throws IllegalArgumentException {
        
        if (command.getIdContenuDetaille() == null || command.getIdContenuDetaille() <= 0) {
            throw new IllegalArgumentException("IdContenuDetaille is required and must be positive");
        }
    }
}
