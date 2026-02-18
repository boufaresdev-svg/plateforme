package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.payTranchePaiement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayTranchePaiementResponse {
    private String id;
    private String message;
    private boolean success;
    
    public static PayTranchePaiementResponse success(String id) {
        return new PayTranchePaiementResponse(id, "Tranche de paiement payée avec succès", true);
    }
    
    public static PayTranchePaiementResponse error(String message) {
        return new PayTranchePaiementResponse(null, message, false);
    }
}