package com.example.BacK.domain.g_RH;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_RH.enumEntity.StatutFichePaie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FichePaie extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private LocalDate dateEmission;
    private double salaireDeBase;
    private double primes;
    private double retenues;
    private double netAPayer;

    @Enumerated(EnumType.STRING)
    private StatutFichePaie statut;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
