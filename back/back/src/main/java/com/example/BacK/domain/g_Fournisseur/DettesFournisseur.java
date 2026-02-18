package com.example.BacK.domain.g_Fournisseur;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Fournisseur.enumEntity.TypeIndividu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DettesFournisseur extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String numeroFacture;
    private String titre;
    private String description;
    private Float montantTotal;
    private Boolean estPaye;
    
    @Enumerated(EnumType.STRING)
    private TypeIndividu type;
    
    private LocalDate datePaiementPrevue;
    private LocalDate datePaiementReelle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;
    
    @OneToMany(mappedBy = "dettesFournisseur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TranchePaiement> tranchesPaiement = new ArrayList<>();
}