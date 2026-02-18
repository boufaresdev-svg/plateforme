package com.example.BacK.application.g_Stock.Command.stock.addStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStockResponse {
    
    private String id;
    private String articleId;
    private String entrepotId;
    private String fournisseurId;
    private Integer quantite;
    private LocalDateTime dateDexpiration;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}