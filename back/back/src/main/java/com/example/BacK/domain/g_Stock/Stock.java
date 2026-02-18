package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Stock extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false)
    private Article article;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entrepot_id", referencedColumnName = "id", nullable = false)
    private Entrepot entrepot;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id", referencedColumnName = "id")
    private Fournisseur fournisseur;
    
    @Column(name = "quantite", nullable = false)
    private Integer quantite;
    
    @Column(name = "date_dexpiration")
    private LocalDateTime dateDexpiration;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
