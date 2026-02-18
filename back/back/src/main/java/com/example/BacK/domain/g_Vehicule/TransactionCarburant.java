package com.example.BacK.domain.g_Vehicule;
import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Vehicule.enumEntity.TypeCarburant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionCarburant extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private LocalDate date;
    private String station;
    private String adresseStation;
    private Double quantite;    // litres
    private Double prixLitre;
    private Double montantTotal;
    private Double kilometrage;
    private Double ancienkilometrage;
    private double consommation;
    private String carteTelepeageId;
    private Double montantTelepeage;

    @Enumerated(EnumType.STRING)
    private TypeCarburant typeCarburant;

    private String conducteur;

    @ManyToOne
    @JoinColumn(name = "carte_id", nullable = false)
    private CarteGazoil carte;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;
}
