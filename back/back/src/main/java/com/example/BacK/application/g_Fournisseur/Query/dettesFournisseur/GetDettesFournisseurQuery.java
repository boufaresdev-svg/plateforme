package com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDettesFournisseurQuery {
    private String id;
    private String numeroFacture;
    private String titre;
    private Boolean estPaye;
    private LocalDate datePaiementPrevue;
    private LocalDate datePaiementReelle;
    private String fournisseurId;
    private String fournisseurNom;
    
    // Flexible search term - searches across multiple fields (numeroFacture, titre, description, fournisseurNom)
    private String searchTerm;
    
    // Pagination parameters
    @Min(value = 0, message = "Le numéro de page doit être supérieur ou égal à 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "La taille de page doit être supérieure ou égale à 1")
    private Integer size = 10;
    
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    
    @AssertTrue(message = "La date de paiement prévue doit être antérieure ou égale à la date de paiement réelle")
    private boolean isDatePaiementValid() {
        if (datePaiementPrevue == null || datePaiementReelle == null) {
            return true; // Skip validation if dates are optional
        }
        return !datePaiementPrevue.isAfter(datePaiementReelle);
    }
}