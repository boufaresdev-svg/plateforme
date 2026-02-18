package com.example.BacK.domain.g_Fournisseur;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TranchePaiement extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    private Double montant;
    private LocalDate dateEcheance;
    private LocalDate datePaiement;
    private Boolean estPaye;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dettes_fournisseur_id")
    private DettesFournisseur dettesFournisseur;
}