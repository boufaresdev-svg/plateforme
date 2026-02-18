package com.example.BacK.application.g_Fournisseur.Command.fournisseur.updateFournisseur;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFournisseurCommand {
    

    private String id;
    
    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    private String nom;
    
    private String infoContact;
    private String adresse;
    private String telephone;
    private String matriculeFiscale;
}