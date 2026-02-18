package com.example.BacK.application.g_Stock.Query.tracabilite;

import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTracabiliteResponse {
    
    private String id;
    private TypeMouvement typeMouvement;
    private String articleId;
    private String articleNom;
    private String articleSku;
    private String entrepotId;
    private String entrepotNom;
    private Integer quantite;
    private Integer quantitePrecedente;
    private Integer quantiteActuelle;
    private LocalDateTime dateMouvement;
    private String utilisateurId;
    private String utilisateurNom;
    private String reference;
    private String motif;
    private String commentaire;
    private Double prixUnitaire;
    private Double valeurTotale;
    private LocalDateTime createdAt;
    private String createdBy;
}
