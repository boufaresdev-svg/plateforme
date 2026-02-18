package com.example.BacK.application.g_Stock.Query.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStockQuery {
    
    private String id;
    private String articleId;
    private String entrepotId;
    private String categorieId;
    private String marqueId;
    private String fournisseurId;
    private String statut;
    private Integer page;
    private Integer size;
}