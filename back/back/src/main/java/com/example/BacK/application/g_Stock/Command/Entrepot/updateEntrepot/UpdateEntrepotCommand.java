package com.example.BacK.application.g_Stock.Command.entrepot.updateEntrepot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEntrepotCommand {
    
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
    private String statut;
    private Boolean estActif;
}
