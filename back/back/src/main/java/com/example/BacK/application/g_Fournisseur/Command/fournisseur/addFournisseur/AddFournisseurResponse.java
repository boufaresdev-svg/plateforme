package com.example.BacK.application.g_Fournisseur.Command.fournisseur.addFournisseur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddFournisseurResponse {
    private String id;
    private String message;
    
    public AddFournisseurResponse(String id) {
        this.id = id;
        this.message = "Fournisseur créé avec succès";
    }
}