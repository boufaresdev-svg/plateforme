package com.example.BacK.application.g_Stock.Query.stockLot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStockLotResponse {
    private String id;
    private String numeroLot;
    private String articleId;
    private String articleNom;
    private String articleSku;
    private String entrepotId;
    private String entrepotNom;
    private Integer quantiteInitiale;
    private Integer quantiteActuelle;
    private Integer quantiteReservee;
    private Integer quantiteDisponible;
    private LocalDate dateAchat;
    private Double prixAchatUnitaire;
    private Double prixVenteUnitaire;
    private Double valeurTotale;
    private LocalDate dateExpiration;
    private String factureUrl;
    private String numeroFacture;
    private String referenceFournisseur;
    private String notes;
    private Boolean estActif;
}
