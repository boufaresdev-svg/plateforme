package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.addDettesFournisseur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDettesFournisseurResponse {
    private String id;
    private String message;
    
    public AddDettesFournisseurResponse(String id) {
        this.id = id;
        this.message = "DettesFournisseur créée avec succès";
    }
}