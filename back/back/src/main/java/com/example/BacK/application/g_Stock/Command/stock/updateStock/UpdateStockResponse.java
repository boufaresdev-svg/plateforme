package com.example.BacK.application.g_Stock.Command.stock.updateStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStockResponse {
    
    private String id;
    private String articleId;
    private String entrepotId;
    private String fournisseurId;
    private Integer quantite;
    private LocalDateTime dateDexpiration;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}