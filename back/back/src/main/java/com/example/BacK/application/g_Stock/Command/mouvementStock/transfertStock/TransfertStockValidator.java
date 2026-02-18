package com.example.BacK.application.g_Stock.Command.mouvementStock.transfertStock;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.domain.g_Stock.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransfertStockValidator {
    
    private final IArticleRepositoryService articleService;
    private final IEntrepotRepositoryService entrepotService;
    private final IStockRepositoryService stockService;
    
    public List<String> validate(TransfertStockCommand command) {
        List<String> errors = new ArrayList<>();
        
        // Validate article exists
        if (!articleService.existsById(command.getArticleId())) {
            errors.add("L'article avec l'ID " + command.getArticleId() + " n'existe pas");
        }
        
        // Validate source warehouse exists
        if (!entrepotService.existsById(command.getSourceEntrepotId())) {
            errors.add("L'entrepôt source avec l'ID " + command.getSourceEntrepotId() + " n'existe pas");
        }
        
        // Validate destination warehouse exists
        if (!entrepotService.existsById(command.getDestinationEntrepotId())) {
            errors.add("L'entrepôt destination avec l'ID " + command.getDestinationEntrepotId() + " n'existe pas");
        }
        
        // Validate warehouses are different
        if (command.getSourceEntrepotId().equals(command.getDestinationEntrepotId())) {
            errors.add("L'entrepôt source et destination doivent être différents");
        }
        
        // Validate quantity
        if (command.getQuantite() == null || command.getQuantite() <= 0) {
            errors.add("La quantité doit être supérieure à zéro");
        }
        
        // Validate stock availability in source warehouse
        if (errors.isEmpty()) {
            Stock stock = stockService.findByArticleIdAndEntrepotId(
                command.getArticleId(), 
                command.getSourceEntrepotId()
            );
            
            if (stock == null) {
                errors.add("Aucun stock disponible pour cet article dans l'entrepôt source");
            } else {
                if (stock.getQuantite() < command.getQuantite()) {
                    errors.add(String.format(
                        "Stock insuffisant dans l'entrepôt source. Disponible: %.2f, Demandé: %.2f",
                        stock.getQuantite().doubleValue(),
                        command.getQuantite()
                    ));
                }
            }
        }
        
        return errors;
    }
}
