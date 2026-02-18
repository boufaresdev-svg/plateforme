package com.example.BacK.domain.g_Projet;


import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Projet.enumEntity.PhaseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Phase extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    private String nom;
    private String description;
    private Integer ordre;
    private String statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;
    private Double budget;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions = new ArrayList<>();


}
