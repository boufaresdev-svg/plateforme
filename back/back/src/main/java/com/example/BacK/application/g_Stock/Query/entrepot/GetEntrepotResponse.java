package com.example.BacK.application.g_Stock.Query.entrepot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEntrepotResponse {
    
    private String id;
    private String nom;
    private String description;
    private String adresse;
    private String ville;
    private String codePostal;
    private String telephone;
    private String email;
    private String responsable;
    private Double superficie;
    private Double capaciteMaximale;
    private Double capaciteUtilisee;
    private String statut;
    private Boolean estActif;
    private Long nombreProduits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
