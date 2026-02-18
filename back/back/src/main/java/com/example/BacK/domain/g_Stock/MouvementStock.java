package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeEntree;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeSortie;
import com.example.BacK.domain.g_Utilisateurs.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MOUVEMENT_STOCK")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MouvementStock extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", length = 20, nullable = false)
    private TypeMouvement typeMouvement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_entrepot_id")
    private Entrepot sourceEntrepot;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_entrepot_id")
    private Entrepot destinationEntrepot;
    
    @Column(name = "quantite", nullable = false)
    private Double quantite;
    
    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;
    
    @Column(name = "reference", length = 255)
    private String reference;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private Statut statut;
    
    @Column(name = "numero_bon_reception", length = 100)
    private String numeroBonReception;
    
    @Column(name = "reference_bon_commande", length = 100)
    private String referenceBonCommande;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_entree", length = 20)
    private TypeEntree typeEntree;
    
    @Column(name = "numero_bon_livraison", length = 100)
    private String numeroBonLivraison;
    
    @Column(name = "reference_commande_client", length = 100)
    private String referenceCommandeClient;
    
    @Column(name = "destinataire", length = 255)
    private String destinataire;
    
    @Column(name = "motif", length = 500)
    private String motif;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_sortie", length = 20)
    private TypeSortie typeSortie;
    
    @Column(name = "prix_unitaire")
    private Double prixUnitaire;
    
    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Batch/Lot information for entry
    @Column(name = "prix_achat_unitaire")
    private Double prixAchatUnitaire;
    
    @Column(name = "prix_vente_unitaire")
    private Double prixVenteUnitaire;
    
    @Column(name = "facture_url", length = 500)
    private String factureUrl;
    
    @Column(name = "numero_facture", length = 100)
    private String numeroFacture;
    
    // Batch selection for exit (which batch to take from)
    @Column(name = "stock_lot_id", length = 36)
    private String stockLotId;
    
    @Column(name = "created_by", length = 36)
    private String createdBy;
    
    @Column(name = "updated_by", length = 36)
    private String updatedBy;
}
