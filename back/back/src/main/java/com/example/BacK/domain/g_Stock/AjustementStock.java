package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Utilisateurs.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "AJUSTEMENT_STOCK")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AjustementStock extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false)
    private Article article;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entrepot_id", referencedColumnName = "id")
    private Entrepot entrepot;
    
    @Column(name = "quantite_avant", nullable = false)
    private int quantiteAvant;
    
    @Column(name = "quantite_apres", nullable = false)
    private int quantiteApres;
    
    @Column(name = "ajustement", nullable = false)
    private int ajustement;
    
    @Column(name = "date_ajustement", nullable = false)
    private LocalDate dateAjustement;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "id", nullable = false)
    private User utilisateur;
    
    @Column(name = "raison", columnDefinition = "TEXT")
    private String raison;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    public void calculateAjustement() {
        this.ajustement = this.quantiteApres - this.quantiteAvant;
        if (this.dateAjustement == null) {
            this.dateAjustement = LocalDate.now();
        }
    }
}
