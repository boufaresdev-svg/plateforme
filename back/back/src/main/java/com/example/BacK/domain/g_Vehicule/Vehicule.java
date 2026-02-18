package com.example.BacK.domain.g_Vehicule;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vehicule extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String serie;   // ex: xxx tn xxxx
    private String marque;
    private LocalDate dateVisiteTechnique;
    private LocalDate dateAssurance;
    private LocalDate dateTaxe;
    private Double prochainVidangeKm;
    private Double kmActuel;
    private Double prochaineChaineKm ;
    private double consommation100km ;
    private LocalDate dateChangementBatterie;

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reparation> reparations = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionCarburant> transactions = new HashSet<>();
}
