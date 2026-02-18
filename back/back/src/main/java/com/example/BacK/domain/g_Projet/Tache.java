package com.example.BacK.domain.g_Projet;

// Tache.java
import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteTache;
import com.example.BacK.domain.g_Projet.enumEntity.StatutTache;
import com.example.BacK.domain.g_RH.Employee;
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
public class Tache extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String nom;
    private String description;

    @Enumerated(EnumType.STRING)
    private StatutTache statut;

    @Enumerated(EnumType.STRING)
    private PrioriteTache priorite;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double dureeEstimee;
    private Double dureeReelle;
    private Double progression;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL)
    private List<CommentaireTache> commentaires;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL)
    private List<Charge> charges;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL)
    private List<LivrableTache> livrable;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL)
    private List<ProblemeTache> probleme;

}

