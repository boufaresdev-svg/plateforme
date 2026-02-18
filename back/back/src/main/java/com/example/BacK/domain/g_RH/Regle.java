package com.example.BacK.domain.g_RH;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_RH.enumEntity.StatutRegle;
import com.example.BacK.domain.g_RH.enumEntity.TypeRegle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Regle  extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    private String libelle;
    private String description;
    private int minAnciennete;
    private int maxAnciennete;
    private Double montantFixe;
    private Double pourcentageSalaire;
    private Integer nombreJours;
    private Integer nombrePoints;
    private TypeRegle typeRegle;
    private StatutRegle statut;
    private LocalDate dateCreation;
    private LocalDate dateModification;

    private String conditions;
    private boolean automatique;

    @ManyToOne
    @JoinColumn(name = "prime_id")
    private Prime prime;

    @ManyToOne
    @JoinColumn(name = "retenue_id")
    private Retenue retenue;
}
