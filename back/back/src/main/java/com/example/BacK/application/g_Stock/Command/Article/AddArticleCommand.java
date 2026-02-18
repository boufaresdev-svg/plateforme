package com.example.BacK.application.g_Stock.Command.Article;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddArticleCommand {
    private String sku;
    private String codebare;
    
    @NotBlank(message = "Le nom de l'article est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    private String nom;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    private String categorieId;
    
    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;
    
    @Size(max = 50, message = "L'unité de mesure ne peut pas dépasser 50 caractères")
    private String unitesDeMesure;
    
    @Min(value = 0, message = "Le prix d'achat ne peut pas être négatif")
    private Double prixAchat;
    
    @Min(value = 0, message = "Le prix de vente ne peut pas être négatif")
    private Double prixVente;
    
    @Min(value = 0, message = "Le taux de taxe ne peut pas être négatif")
    @Max(value = 100, message = "Le taux de taxe ne peut pas dépasser 100%")
    private Double tauxTaxe;
    
    private String marqueId;
    
    @Min(value = 0, message = "Le prix de vente HT ne peut pas être négatif")
    private Double prixVenteHT;
    
    @Min(value = 0, message = "Le stock minimum ne peut pas être négatif")
    private Integer stockMinimum;
    
    @Min(value = 0, message = "Le stock maximum ne peut pas être négatif")
    private Integer stockMaximum;
    
    private Boolean estStockBasee;
    private Boolean estStockElever;
    private Boolean estActif;
}
