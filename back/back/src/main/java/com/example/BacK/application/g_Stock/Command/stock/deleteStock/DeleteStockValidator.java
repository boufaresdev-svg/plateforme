package com.example.BacK.application.g_Stock.Command.stock.deleteStock;

import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class DeleteStockValidator {
    
    private final IStockRepositoryService stockRepositoryService;
    
    public DeleteStockValidator(IStockRepositoryService stockRepositoryService) {
        this.stockRepositoryService = stockRepositoryService;
    }
    
    public void validate(DeleteStockCommand command) {
        // Validate stock exists
        var stock = stockRepositoryService.getById(command.getId());
        if (stock == null) {
            throw new IllegalArgumentException("Le stock spécifié n'existe pas");
        }
    }
}