package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.updateDettesFournisseur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDettesFournisseurResponse {
    private String id;
    private String message;
    
    public UpdateDettesFournisseurResponse(String id) {
        this.id = id;
        this.message = "DettesFournisseur mise à jour avec succès";
    }
}