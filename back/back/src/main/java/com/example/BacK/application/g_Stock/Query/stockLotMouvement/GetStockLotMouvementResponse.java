package com.example.BacK.application.g_Stock.Query.stockLotMouvement;

import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStockLotMouvementResponse {
    private String id;
    private String stockLotId;
    private String numeroLot;
    private String mouvementStockId;
    private TypeMouvement typeMouvement;
    private Integer quantite;
    private Integer quantiteAvant;
    private Integer quantiteApres;
    private Double prixUnitaire;
    private Double valeurTotale;
    private LocalDateTime dateMouvement;
    private String utilisateurId;
    private String utilisateurNom;
    private String reference;
    private String commentaire;
    private String articleId;
    private String articleNom;
    private String entrepotId;
    private String entrepotNom;
}
