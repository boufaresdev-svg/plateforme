package com.example.BacK.application.g_Fournisseur.Command.fournisseur.deleteFournisseur;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteFournisseurResponse {
    private String message;
    
    public DeleteFournisseurResponse(String id) {
        this.message = "Fournisseur avec l'id " + id + " supprimé avec succès";
    }
}