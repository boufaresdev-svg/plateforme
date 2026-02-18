package com.example.BacK.application.g_Stock.Command.stock.addStock;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class AddStockValidator {
    
    private final IArticleRepositoryService articleRepositoryService;
    private final IEntrepotRepositoryService entrepotRepositoryService;
    private final IStockRepositoryService stockRepositoryService;
    
    public AddStockValidator(IArticleRepositoryService articleRepositoryService,
                           IEntrepotRepositoryService entrepotRepositoryService,
                           IStockRepositoryService stockRepositoryService) {
        this.articleRepositoryService = articleRepositoryService;
        this.entrepotRepositoryService = entrepotRepositoryService;
        this.stockRepositoryService = stockRepositoryService;
    }
    
    public void validate(AddStockCommand command) {
        // Validate article exists
        var article = articleRepositoryService.findById(command.getArticleId());
        if (article.isEmpty()) {
            throw new IllegalArgumentException("L'article spécifié n'existe pas");
        }
        
        // Validate entrepot exists
        var entrepot = entrepotRepositoryService.findById(command.getEntrepotId());
        if (entrepot.isEmpty()) {
            throw new IllegalArgumentException("L'entrepôt spécifié n'existe pas");
        }
        
        // Check if stock already exists for this article in this entrepot
        var existingStock = stockRepositoryService.getByArticleIdAndEntrepotId(
            command.getArticleId(), command.getEntrepotId());
        if (existingStock != null) {
            throw new IllegalArgumentException("Un stock existe déjà pour cet article dans cet entrepôt");
        }
    }
}