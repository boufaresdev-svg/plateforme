package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ALERTE_STOCK")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlerteStock extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private AlerteType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", length = 20, nullable = false)
    private AlertePriorite priorite;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private AlerteStatut statut;
    
    @Column(name = "titre", length = 255, nullable = false)
    private String titre;
    
    @Column(name = "message", length = 500, nullable = false)
    private String message;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id")
    private Entrepot entrepot;
    
    @Column(name = "quantite_actuelle")
    private Integer quantiteActuelle;
    
    @Column(name = "seuil_minimum")
    private Integer seuilMinimum;
    
    @Column(name = "seuil_critique")
    private Integer seuilCritique;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (statut == null) {
            statut = AlerteStatut.ACTIVE;
        }
        if (isRead == null) {
            isRead = false;
        }
        if (isArchived == null) {
            isArchived = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
