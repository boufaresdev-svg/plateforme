package com.example.BacK.application.g_Fournisseur.Query.fournisseur;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFournisseurQuery {
    private String id;
    private String nom;
    private String infoContact;
    private String adresse;
    private String telephone;
    private String matriculeFiscale;
    
    // Pagination parameters
    @Min(value = 0, message = "Le numéro de page doit être supérieur ou égal à 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "La taille de page doit être supérieure ou égale à 1")
    private Integer size = 10;
    
    private String sortBy = "nom";
    private String sortDirection = "ASC";
}