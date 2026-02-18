package com.example.BacK.domain.g_Projet;

// Mission.java
import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteMission;
import com.example.BacK.domain.g_Projet.enumEntity.StatutMission;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mission  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String nom;
    private String description;
    private String objectif;

    @Enumerated(EnumType.STRING)
    private StatutMission statut;

    @Enumerated(EnumType.STRING)
    private PrioriteMission priorite;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;
    private Double budget;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Phase phase;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tache> taches = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeAffecte> employesAffectes = new ArrayList<>();



}
