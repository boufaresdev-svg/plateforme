package com.example.BacK.application.g_Fournisseur.Query.fournisseur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFournisseurResponse {
    private String id;
    private String nom;
    private String infoContact;
    private String adresse;
    private String telephone;
    private String matriculeFiscale;
    private Integer nombreDettes;
    private Double totalDettes;
    private Double soldeTotal;
    private Double montantAPayer;
}