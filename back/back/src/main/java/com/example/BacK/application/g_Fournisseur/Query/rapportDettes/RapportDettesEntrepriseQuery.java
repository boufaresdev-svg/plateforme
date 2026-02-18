package com.example.BacK.application.g_Fournisseur.Query.rapportDettes;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RapportDettesEntrepriseQuery {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean inclureDettesPayees = true;
    private String fournisseurId;  // Added for single fournisseur filtering
    
    @AssertTrue(message = "La date de début doit être antérieure ou égale à la date de fin")
    private boolean isDateRangeValid() {
        if (dateDebut == null || dateFin == null) {
            return true; // Skip validation if dates are optional
        }
        return !dateDebut.isAfter(dateFin);
    }
}
