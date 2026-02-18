package com.example.BacK.application.g_Stock.Command.mouvementStock.entreeStock;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class EntreeStockValidator {

    private final IArticleRepositoryService articleRepositoryService;
    private final IEntrepotRepositoryService entrepotRepositoryService;

    public EntreeStockValidator(
            IArticleRepositoryService articleRepositoryService,
            IEntrepotRepositoryService entrepotRepositoryService) {
        this.articleRepositoryService = articleRepositoryService;
        this.entrepotRepositoryService = entrepotRepositoryService;
    }

    public void validate(EntreeStockCommand command) {
        // Validate article exists
        if (articleRepositoryService.getById(command.getArticleId()) == null) {
            throw new IllegalArgumentException("L'article avec l'ID " + command.getArticleId() + " n'existe pas");
        }

        // Validate destination warehouse exists
        if (entrepotRepositoryService.getById(command.getDestinationEntrepotId()) == null) {
            throw new IllegalArgumentException("L'entrepôt de destination avec l'ID " + command.getDestinationEntrepotId() + " n'existe pas");
        }

        // Validate quantity
        if (command.getQuantite() == null || command.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }

        // Validate type entree
        if (command.getTypeEntree() == null) {
            throw new IllegalArgumentException("Le type d'entrée est obligatoire");
        }

        // Business validation based on type
        validateByType(command);
    }

    private void validateByType(EntreeStockCommand command) {
        switch (command.getTypeEntree()) {
            case ACHAT:
                // Optional: bon de réception and bon de commande are not mandatory
                break;
            case RETOUR_CLIENT:
                // Optional: reference is helpful but not mandatory
                break;
            case INVENTAIRE_POSITIF:
                // No specific validation for inventory adjustment
                break;
        }
    }
}
