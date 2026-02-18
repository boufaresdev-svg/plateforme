package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.addTranchePaiement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTranchePaiementResponse {
    private String id;
    private String message;
    private boolean success;
    
    public static AddTranchePaiementResponse success(String id) {
        return new AddTranchePaiementResponse(id, "Tranche de paiement créée avec succès", true);
    }
    
    public static AddTranchePaiementResponse error(String message) {
        return new AddTranchePaiementResponse(null, message, false);
    }
}