package com.example.BacK.application.g_Stock.Query.ajustementStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAjustementStockResponse {
    private String id;
    private String articleId;
    private String articleNom;
    private String articleCategorieId;
    private String articleCategorie;
    private String articleMarqueId;
    private String articleMarque;
    private String entrepotId;
    private String entrepotNom;
    private int quantiteAvant;
    private int quantiteApres;
    private int ajustement;
    private LocalDate dateAjustement;
    private String utilisateurId;
    private String utilisateurNom;
    private String raison;
    private String notes;
}
