package com.example.BacK.application.g_Stock.Command.stock.updateStock;

import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class UpdateStockValidator {
    
    private final IStockRepositoryService stockRepositoryService;
    
    public UpdateStockValidator(IStockRepositoryService stockRepositoryService) {
        this.stockRepositoryService = stockRepositoryService;
    }
    
    public void validate(UpdateStockCommand command) {
        // Validate stock exists
        var stock = stockRepositoryService.getById(command.getId());
        if (stock == null) {
            throw new IllegalArgumentException("Le stock spécifié n'existe pas");
        }
    }
}