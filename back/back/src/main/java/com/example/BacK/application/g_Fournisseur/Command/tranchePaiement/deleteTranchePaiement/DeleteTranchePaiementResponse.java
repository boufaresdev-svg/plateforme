package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.deleteTranchePaiement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteTranchePaiementResponse {
    private boolean success;
    private String message;
    
    public static DeleteTranchePaiementResponse success() {
        return new DeleteTranchePaiementResponse(true, "Tranche de paiement supprimée avec succès");
    }
    
    public static DeleteTranchePaiementResponse error(String message) {
        return new DeleteTranchePaiementResponse(false, message);
    }
}
