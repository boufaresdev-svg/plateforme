package com.example.BacK.domain.g_Projet;

import com.example.BacK.domain.g_Projet.enumEntity.RoleProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutAffectation;
import com.example.BacK.domain.g_RH.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EmployeAffecte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String nom;
    private String prenom;
    private String poste;
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleProjet role;

    private LocalDate dateAffectation;
    private Double tauxHoraire;
    private Double heuresAllouees;
    private Double heuresRealisees;

    @Enumerated(EnumType.STRING)
    private StatutAffectation statut;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;
}
