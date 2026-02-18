package com.example.BacK.application.g_Stock.Command.mouvementStock.transfertStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransfertStockResponse {
    private String id;
    private TypeMouvement typeMouvement;
    private String articleId;
    private String articleNom;
    private String articleSku;
    private String sourceEntrepotId;
    private String sourceEntrepotNom;
    private String destinationEntrepotId;
    private String destinationEntrepotNom;
    private Double quantite;
    private String reference;
    private String motif;
    private LocalDateTime dateMouvement;
    private Statut statut;
    private String utilisateurId;
    private String utilisateurNom;
    private String stockLotId; // Source lot ID
}
