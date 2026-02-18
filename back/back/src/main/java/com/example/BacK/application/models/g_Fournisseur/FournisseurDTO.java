package com.example.BacK.application.models.g_Fournisseur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FournisseurDTO {
    private String id;
    private String nom;
    private String infoContact;
    private String adresse;
    private String telephone;
    private String matriculeFiscale;
}