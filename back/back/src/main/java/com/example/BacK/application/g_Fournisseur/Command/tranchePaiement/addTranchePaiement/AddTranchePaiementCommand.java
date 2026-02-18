package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.addTranchePaiement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTranchePaiementCommand {
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;
    
    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;
    
    private Boolean estPaye = false;
    
    @NotBlank(message = "L'ID de la dette fournisseur est obligatoire")
    private String dettesFournisseurId;
}