package com.example.BacK.application.g_Stock.Command.mouvementStock.sortieStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeSortie;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SortieStockResponse {
    private String id;
    private TypeMouvement typeMouvement;
    private String articleId;
    private String articleNom;
    private String articleSku;
    private String sourceEntrepotId;
    private String sourceEntrepotNom;
    private Double quantite;
    private TypeSortie typeSortie;
    private String reference;
    private String numeroBonLivraison;
    private String referenceCommandeClient;
    private String destinataire;
    private String motif;
    private LocalDateTime dateMouvement;
    private Statut statut;
    private String utilisateurId;
    private String utilisateurNom;
    private String stockLotId; // The batch ID that stock was taken from
}
