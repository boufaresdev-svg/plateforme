package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.updateTranchePaiement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTranchePaiementCommand {
    
    private String id;
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;
    
    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;
    
    private Boolean estPaye;
}
