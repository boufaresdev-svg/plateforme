package com.example.BacK.application.g_Stock.Command.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArticleCommand {
    private String id;
    private String sku;
    private String codebare;
    private String nom;
    private String description;
    private String categorieId;
    private String imageUrl;
    private String unitesDeMesure;
    private Double prixAchat;
    private Double prixVente;
    private Double tauxTaxe;
    private String marqueId;
    private Double prixVenteHT;
    private Integer stockMinimum;
    private Integer stockMaximum;
    private Boolean estStockBasee;
    private Boolean estStockElever;
    private Boolean estActif;
}
