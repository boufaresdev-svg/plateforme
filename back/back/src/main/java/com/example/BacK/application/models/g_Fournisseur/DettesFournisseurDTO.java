package com.example.BacK.application.models.g_Fournisseur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DettesFournisseurDTO {
    private String id;
    private Boolean estPaye;
    private LocalDate datePaiementPrevue;
    private LocalDate datePaiementReelle;
    private String fournisseurId;
    private String fournisseurNom;
}