package com.example.BacK.domain.g_RH;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_RH.enumEntity.StatutConge;
import com.example.BacK.domain.g_RH.enumEntity.TypeConge;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Congee  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    private TypeConge type;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int nombreJours;
    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutConge statut;

    private LocalDate dateValidation;
    private String validePar;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
