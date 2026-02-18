package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.updateTranchePaiement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTranchePaiementResponse {
    
    private boolean success;
    private String message;
    private String trancheId;
    
    public static UpdateTranchePaiementResponse success(String trancheId) {
        return new UpdateTranchePaiementResponse(true, "Tranche de paiement mise à jour avec succès", trancheId);
    }
    
    public static UpdateTranchePaiementResponse error(String message) {
        return new UpdateTranchePaiementResponse(false, message, null);
    }
}
