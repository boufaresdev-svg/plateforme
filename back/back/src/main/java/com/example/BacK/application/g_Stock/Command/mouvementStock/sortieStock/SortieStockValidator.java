package com.example.BacK.application.g_Stock.Command.mouvementStock.sortieStock;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SortieStockValidator {
    
    private final IArticleRepositoryService articleService;
    private final IEntrepotRepositoryService entrepotService;
    
    public List<String> validate(SortieStockCommand command) {
        List<String> errors = new ArrayList<>();
        
        // Validate article exists
        if (!articleService.existsById(command.getArticleId())) {
            errors.add("L'article avec l'ID " + command.getArticleId() + " n'existe pas");
        }
        
        // Validate source warehouse exists
        if (!entrepotService.existsById(command.getSourceEntrepotId())) {
            errors.add("L'entrepôt source avec l'ID " + command.getSourceEntrepotId() + " n'existe pas");
        }
        
        // Validate quantity
        if (command.getQuantite() == null || command.getQuantite() <= 0) {
            errors.add("La quantité doit être supérieure à zéro");
        }
        
        // Validate type sortie
        if (command.getTypeSortie() == null) {
            errors.add("Le type de sortie est obligatoire");
        }
        
        // Note: Stock lot quantity validation is done in the handler
        // after lot is loaded, since we need to check quantiteDisponible
        
        return errors;
    }
}
