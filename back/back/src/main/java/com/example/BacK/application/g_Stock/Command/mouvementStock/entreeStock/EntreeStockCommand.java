package com.example.BacK.application.g_Stock.Command.mouvementStock.entreeStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeEntree;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntreeStockCommand {
    
    @NotBlank(message = "L'ID de l'article est obligatoire")
    private String articleId;
    
    @NotBlank(message = "L'ID de l'entrepôt de destination est obligatoire")
    private String destinationEntrepotId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;
    
    @NotNull(message = "Le type d'entrée est obligatoire")
    private TypeEntree typeEntree;
    
    @Size(max = 255, message = "La référence ne peut pas dépasser 255 caractères")
    private String reference;
    
    @Size(max = 100, message = "Le numéro de bon de réception ne peut pas dépasser 100 caractères")
    private String numeroBonReception;
    
    @Size(max = 100, message = "La référence de bon de commande ne peut pas dépasser 100 caractères")
    private String referenceBonCommande;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateMouvement;
    
    private Statut statut = Statut.EN_ATTENTE;
    
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
    
    // Batch/Lot information - Required for stock entry
    @NotNull(message = "Le prix d'achat unitaire est obligatoire")
    @Positive(message = "Le prix d'achat doit être positif")
    private Double prixAchatUnitaire;
    
    @NotNull(message = "Le prix de vente unitaire est obligatoire")
    @Positive(message = "Le prix de vente doit être positif")
    private Double prixVenteUnitaire;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAchat;
    
    @Size(max = 500, message = "L'URL de la facture ne peut pas dépasser 500 caractères")
    private String factureUrl;
    
    @Size(max = 100, message = "Le numéro de facture ne peut pas dépasser 100 caractères")
    private String numeroFacture;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateExpiration;
    
    @Size(max = 100, message = "Le numéro de lot ne peut pas dépasser 100 caractères")
    private String numeroLot; // Optional: if not provided, will be auto-generated
    
    @Size(max = 36, message = "L'ID du lot ne peut pas dépasser 36 caractères")
    private String stockLotId; // Optional: if provided, adds quantity to existing lot instead of creating new one
}
