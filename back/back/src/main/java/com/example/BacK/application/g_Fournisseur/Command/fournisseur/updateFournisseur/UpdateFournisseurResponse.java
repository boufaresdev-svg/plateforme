package com.example.BacK.application.g_Fournisseur.Command.fournisseur.updateFournisseur;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateFournisseurResponse {
    private String id;
    private String message;
    
    public UpdateFournisseurResponse(String id) {
        this.id = id;
        this.message = "Fournisseur mis à jour avec succès";
    }
}