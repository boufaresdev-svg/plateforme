package com.example.BacK.application.g_Stock.Command.stock.deleteStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteStockResponse {
    
    private boolean success;
    private String message;
    
    public DeleteStockResponse(boolean success) {
        this.success = success;
        this.message = success ? "Stock supprimé avec succès" : "Échec de la suppression du stock";
    }
}