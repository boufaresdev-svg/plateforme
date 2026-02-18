package com.example.BacK.application.g_Stock.Command.entrepot.addEntrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class AddEntrepotValidator {

    private final IEntrepotRepositoryService entrepotRepositoryService;

    public AddEntrepotValidator(IEntrepotRepositoryService entrepotRepositoryService) {
        this.entrepotRepositoryService = entrepotRepositoryService;
    }

    public void validate(AddEntrepotCommand command) {
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'entrepôt est obligatoire");
        }

        if (command.getAdresse() == null || command.getAdresse().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse est obligatoire");
        }

        if (command.getVille() == null || command.getVille().trim().isEmpty()) {
            throw new IllegalArgumentException("La ville est obligatoire");
        }

        if (entrepotRepositoryService.findByNom(command.getNom()).isPresent()) {
            throw new IllegalArgumentException("Un entrepôt avec ce nom existe déjà");
        }

        if (command.getSuperficie() != null && command.getSuperficie() < 0) {
            throw new IllegalArgumentException("La superficie ne peut pas être négative");
        }

        if (command.getCapaciteMaximale() != null && command.getCapaciteMaximale() < 0) {
            throw new IllegalArgumentException("La capacité maximale ne peut pas être négative");
        }
    }
}
