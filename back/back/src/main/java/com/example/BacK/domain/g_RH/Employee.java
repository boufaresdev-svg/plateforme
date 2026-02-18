package com.example.BacK.domain.g_RH;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Projet.Charge;
import com.example.BacK.domain.g_RH.enumEntity.SituationFamiliale;
import com.example.BacK.domain.g_RH.enumEntity.StatutEmployee;
import com.example.BacK.domain.g_RH.enumEntity.TypePieceIdentite;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String poste;
    private String departement;
    private LocalDate dateEmbauche;
    private double salaire;
    private int soldeConges;
    private int congesUtilises;
    private int soldePoints;
    private int pointsDemandesParAn;
    private String adresse;

    @Enumerated(EnumType.STRING)
    private TypePieceIdentite typePieceIdentite;

    @Column(unique = true, nullable = false)
    private String numeroPieceIdentite;

    private int nombreEnfants;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Congee> conges;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prime> primes;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Retenue> retenues;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Charge> charges;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<FichePaie> fichesPaie ;
}

