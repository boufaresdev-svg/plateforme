package com.example.BacK.application.g_Stock.Command.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArticleCommandResponse {
    private String id;
    private String sku;
    private String codebare;
    private String nom;
    private String description;
    private String categorie;
    private String imageUrl;
    private String unitesDeMesure;
    private Double prixAchat;
    private Double prixVente;
    private Double tauxTaxe;
    private String marque;
    private Double prixVenteHT;
    private Integer stockMinimum;
    private Integer stockMaximum;
    private String fournisseur;
    private Boolean estStockBasee;
    private Boolean estStockElever;
    private Boolean estActif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
