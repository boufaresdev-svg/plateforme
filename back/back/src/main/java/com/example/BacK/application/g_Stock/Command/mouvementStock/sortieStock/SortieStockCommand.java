package com.example.BacK.application.g_Stock.Command.mouvementStock.sortieStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeSortie;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SortieStockCommand {
    
    @NotBlank(message = "L'ID de l'article est obligatoire")
    private String articleId;
    
    @NotBlank(message = "L'ID de l'entrepôt source est obligatoire")
    private String sourceEntrepotId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Double quantite;
    
    @NotNull(message = "Le type de sortie est obligatoire")
    private TypeSortie typeSortie;
    
    private String reference;
    
    private String numeroBonLivraison;
    
    private String referenceCommandeClient;
    
    private String destinataire;
    
    private String motif;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateMouvement;
    
    private Statut statut = Statut.EN_ATTENTE;
    
    private String notes;
    
    // Batch selection - REQUIRED for stock exit
    // User must specify which batch to take stock from
    @NotBlank(message = "L'ID du lot de stock est obligatoire pour une sortie")
    private String stockLotId;
}
