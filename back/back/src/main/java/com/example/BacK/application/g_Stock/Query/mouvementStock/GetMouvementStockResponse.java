package com.example.BacK.application.g_Stock.Query.mouvementStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeEntree;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeSortie;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class GetMouvementStockResponse {
    
    private String id;
    private TypeMouvement typeMouvement;
    private String articleId;
    private String articleNom;
    private String articleSku;
    private String sourceEntrepotId;
    private String sourceEntrepotNom;
    private String destinationEntrepotId;
    private String destinationEntrepotNom;
    private Integer quantite;
    private LocalDateTime dateMouvement;
    private String utilisateurId;
    private String utilisateurNom;
    private String reference;
    private Statut statut;
    private String numeroBonReception;
    private String referenceBonCommande;
    private TypeEntree typeEntree;
    private String numeroBonSortie;
    private TypeSortie typeSortie;
    private String motif;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String stockLotId; // Batch/Lot ID for tracking
}
