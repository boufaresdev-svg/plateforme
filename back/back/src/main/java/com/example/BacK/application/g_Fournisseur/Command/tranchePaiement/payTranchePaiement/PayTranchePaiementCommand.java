package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.payTranchePaiement;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayTranchePaiementCommand {
    
    @NotBlank(message = "L'ID de la tranche de paiement est obligatoire")
    private String trancheId;
}