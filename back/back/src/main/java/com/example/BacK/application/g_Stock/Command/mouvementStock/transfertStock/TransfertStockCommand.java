package com.example.BacK.application.g_Stock.Command.mouvementStock.transfertStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransfertStockCommand {
    
    @NotBlank(message = "L'ID de l'article est obligatoire")
    private String articleId;
    
    @NotBlank(message = "L'ID de l'entrepôt source est obligatoire")
    private String sourceEntrepotId;
    
    @NotBlank(message = "L'ID de l'entrepôt destination est obligatoire")
    private String destinationEntrepotId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Double quantite;
    
    private String reference;
    
    private String motif;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateMouvement;
    
    private Statut statut = Statut.EN_ATTENTE;
    
    private String notes;
    
    // Batch selection - REQUIRED for stock transfer
    // User must specify which batch to transfer from
    @NotBlank(message = "L'ID du lot de stock est obligatoire pour un transfert")
    private String stockLotId;
}
