package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.deleteDettesFournisseur;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteDettesFournisseurResponse {
    private String message;
    
    public DeleteDettesFournisseurResponse(String id) {
        this.message = "DettesFournisseur avec l'id " + id + " supprimée avec succès";
    }
}