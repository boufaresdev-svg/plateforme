package com.example.BacK.application.g_Stock.Query.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStockResponse {
    
    private String id;
    private String articleId;
    private String articleNom;
    private String articleDescription;
    private String articleCodebare;
    private Double articlePrixVente;
    private Double articlePrixAchat;
    private Integer articleStockMinimum;
    private Integer articleStockMaximum;
    private String categorieId;
    private String categorieNom;
    private String marqueId;
    private String marqueNom;
    private String entrepotId;
    private String entrepotNom;
    private String entrepotAdresse;
    private String fournisseurId;
    private String fournisseurNom;
    private Integer quantite;
    private LocalDateTime dateDexpiration;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Lot-based pricing fields (calculated from active lots)
    private Double prixUnitaireMoyenAchat;  // Weighted average purchase price from lots
    private Double prixUnitaireMoyenVente;  // Weighted average selling price from lots
    private Double valeurTotaleStock;       // Total value based on lot prices (quantity * purchase price)
    private Integer nombreLots;             // Number of active lots
}