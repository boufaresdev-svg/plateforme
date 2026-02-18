package com.example.BacK.application.g_Stock.Command.entrepot.updateEntrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.domain.g_Stock.Entrepot;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UpdateEntrepotValidator {

    private final IEntrepotRepositoryService entrepotRepositoryService;

    public UpdateEntrepotValidator(IEntrepotRepositoryService entrepotRepositoryService) {
        this.entrepotRepositoryService = entrepotRepositoryService;
    }

    public void validate(UpdateEntrepotCommand command) {
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de l'entrepôt est obligatoire");
        }

        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'entrepôt est obligatoire");
        }

        if (command.getAdresse() == null || command.getAdresse().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse est obligatoire");
        }

        if (command.getVille() == null || command.getVille().trim().isEmpty()) {
            throw new IllegalArgumentException("La ville est obligatoire");
        }

        Optional<Entrepot> existingEntrepot = entrepotRepositoryService.findByNom(command.getNom());
        if (existingEntrepot.isPresent() && !existingEntrepot.get().getId().equals(command.getId())) {
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
