package com.example.BacK.application.g_Fournisseur.Query.tranchePaiement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTranchePaiementResponse {
    private String id;
    private Double montant;
    private LocalDate dateEcheance;
    private LocalDate datePaiement;
    private Boolean estPaye;
    private Boolean estEnRetard;
    private String dettesFournisseurId;
    private String numeroFacture;
    private String fournisseurNom;
}