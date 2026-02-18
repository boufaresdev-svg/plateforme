package com.example.BacK.application.g_Stock.Command.mouvementStock.entreeStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntreeStockResponse {
    private String id;
    private String message;
    
    public EntreeStockResponse(String id) {
        this.id = id;
        this.message = "Entrée de stock créée avec succès";
    }
}
