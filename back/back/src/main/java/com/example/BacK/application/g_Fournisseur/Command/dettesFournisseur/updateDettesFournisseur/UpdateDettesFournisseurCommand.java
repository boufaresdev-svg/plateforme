package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.updateDettesFournisseur;

import com.example.BacK.domain.g_Fournisseur.enumEntity.TypeIndividu;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDettesFournisseurCommand {
    

    private String id;
    
    private String numeroFacture;
    private String titre;
    private String description;
    
    @Positive(message = "Le montant total doit être positif")
    private Float montantTotal;
    
    private Boolean estPaye;
    private TypeIndividu type;
    private LocalDate datePaiementPrevue;
    private LocalDate datePaiementReelle;
    private String fournisseurId;
}