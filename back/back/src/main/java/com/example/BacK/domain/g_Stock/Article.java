package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ARTICLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Article extends Auditable {
    
    @Id
    @Column(name = "id", length = 255)
    private String id;
    
    @Column(name = "sku", unique = true, length = 255)
    private String sku;
    
    @Column(name = "codebare", length = 255)
    private String codebare;
    
    @Column(name = "nom", nullable = false, length = 255)
    private String nom;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id", referencedColumnName = "id")
    private Category categorie;
    
    @Column(name = "imageUrl", length = 500)
    private String imageUrl;
    
    @Column(name = "unitesDeMesure", length = 100)
    private String unitesDeMesure;
    
    @Column(name = "prixAchat")
    private Double prixAchat;
    
    @Column(name = "prixVente")
    private Double prixVente;
    
    @Column(name = "tauxTaxe")
    private Double tauxTaxe;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marque_id", referencedColumnName = "id")
    private Marque marque;
    
    @Column(name = "prixVenteHT")
    private Double prixVenteHT;
    
    @Column(name = "stockMinimum")
    private Integer stockMinimum;
    
    @Column(name = "stockMaximum")
    private Integer stockMaximum;
    
    @Column(name = "estStockBasee")
    private Boolean estStockBasee;
    
    @Column(name = "estStockElever")
    private Boolean estStockElever;
    
    @Column(name = "estActif")
    private Boolean estActif;
    
    @Transient
    private Integer quantiteDisponible;
    
    @PrePersist
    protected void onCreate() {
        if (id == null || id.isEmpty()) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (sku == null || sku.isEmpty()) {
            sku = generateSKU();
        }
    }
    
    private String generateSKU() {
        // Generate SKU with format: ART-YYYYMMDD-XXXX (where XXXX is random 4 digits)
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = now.format(formatter);
        
        // Generate random 4-digit number
        int randomNum = (int) (Math.random() * 9000) + 1000; // Ensures 4 digits (1000-9999)
        
        return "ART-" + datePart + "-" + randomNum;
    }
    
}
