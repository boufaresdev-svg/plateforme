package com.example.BacK.application.g_Stock.Command.stock.addStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStockCommand {
    
    @NotBlank(message = "L'ID de l'article est obligatoire")
    private String articleId;
    
    @NotBlank(message = "L'ID de l'entrepôt est obligatoire")
    private String entrepotId;
    
    private String fournisseurId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantite;
    
    private LocalDateTime dateDexpiration;
    
    private String createdBy;
    
    // Lot/Batch information for initial stock entry
    @Positive(message = "Le prix d'achat doit être positif")
    private Double prixAchatUnitaire;
    
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
}