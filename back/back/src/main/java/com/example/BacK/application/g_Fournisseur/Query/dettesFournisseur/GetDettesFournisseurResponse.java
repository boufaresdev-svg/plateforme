package com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur;

import com.example.BacK.domain.g_Fournisseur.enumEntity.TypeIndividu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDettesFournisseurResponse {
    private String id;
    private String numeroFacture;
    private String titre;
    private String description;
    private Float montantTotal;
    private Boolean estPaye;
    private TypeIndividu type;
    private LocalDate datePaiementPrevue;
    private LocalDate datePaiementReelle;
    private String fournisseurId;
    private String fournisseurNom;
    private Double soldeRestant;
    private Double montantDu;  // Same as soldeRestant, for frontend compatibility
    private Boolean enRetard;
}