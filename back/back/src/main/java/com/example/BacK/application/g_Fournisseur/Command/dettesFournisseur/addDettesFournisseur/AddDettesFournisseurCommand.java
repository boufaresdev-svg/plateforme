package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.addDettesFournisseur;

import com.example.BacK.domain.g_Fournisseur.enumEntity.TypeIndividu;
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
public class AddDettesFournisseurCommand {
    
    @NotBlank(message = "Le numéro de facture est obligatoire")
    private String numeroFacture;
    private String titre;
    private String description;
    
    @NotNull(message = "Le montant total est obligatoire")
    @Positive(message = "Le montant total doit être positif")
    private Float montantTotal;
    
    private Boolean estPaye = false;
    private TypeIndividu type;
    
    @NotNull(message = "La date de paiement prévue est obligatoire")
    private LocalDate datePaiementPrevue;
    
    private LocalDate datePaiementReelle;
    
    @NotBlank(message = "L'ID du fournisseur est obligatoire")
    private String fournisseurId;
}