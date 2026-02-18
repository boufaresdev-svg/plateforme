package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a batch (lot) of stock for a product.
 * Each batch tracks its own purchase details, pricing, and quantity.
 * This enables FIFO/LIFO and price history tracking.
 */
@Entity
@Table(name = "STOCK_LOT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StockLot extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id", nullable = false)
    private Entrepot entrepot;
    
    @Column(name = "numero_lot", length = 100, nullable = false)
    private String numeroLot;
    
    @Column(name = "quantite_initiale", nullable = false)
    private Integer quantiteInitiale;
    
    @Column(name = "quantite_actuelle", nullable = false)
    private Integer quantiteActuelle;
    
    @Column(name = "quantite_reservee")
    private Integer quantiteReservee = 0;
    
    @Column(name = "date_achat", nullable = false)
    private LocalDate dateAchat;
    
    @Column(name = "prix_achat_unitaire", nullable = false)
    private Double prixAchatUnitaire;
    
    @Column(name = "prix_vente_unitaire", nullable = false)
    private Double prixVenteUnitaire;
    
    @Column(name = "date_expiration")
    private LocalDate dateExpiration;
    
    @Column(name = "facture_url", length = 500)
    private String factureUrl;
    
    @Column(name = "numero_facture", length = 100)
    private String numeroFacture;
    
    @Column(name = "reference_fournisseur", length = 255)
    private String referenceFournisseur;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "est_actif")
    private Boolean estActif = true;
    
    @Column(name = "created_by", length = 36)
    private String createdBy;
    
    @Column(name = "updated_by", length = 36)
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        if (numeroLot == null || numeroLot.isEmpty()) {
            numeroLot = generateNumeroLot();
        }
        if (quantiteReservee == null) {
            quantiteReservee = 0;
        }
        if (estActif == null) {
            estActif = true;
        }
    }
    
    private String generateNumeroLot() {
        // Generate batch number with format: LOT-YYYYMMDD-XXXX
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = now.format(formatter);
        int randomNum = (int) (Math.random() * 9000) + 1000;
        return "LOT-" + datePart + "-" + randomNum;
    }
    
    /**
     * Calculate the total value of this batch (quantity * purchase price)
     */
    @Transient
    public Double getValeurTotale() {
        return quantiteActuelle * prixAchatUnitaire;
    }
    
    /**
     * Calculate available quantity (not reserved)
     */
    @Transient
    public Integer getQuantiteDisponible() {
        return quantiteActuelle - quantiteReservee;
    }
    
    /**
     * Check if batch has available quantity
     */
    @Transient
    public boolean hasAvailableQuantity() {
        return getQuantiteDisponible() > 0;
    }
}
