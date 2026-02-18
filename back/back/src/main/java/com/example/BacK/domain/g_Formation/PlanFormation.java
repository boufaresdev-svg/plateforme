package com.example.BacK.domain.g_Formation;

import com.example.BacK.domain.g_Formation.enumEntity.StatutFormation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"formation", "formateur", "apprenants", "evaluations"})
public class PlanFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlanFormation;
    private String titre;
    private String description;
    private Date dateLancement;
    private Date dateFinReel;
    private Date dateDebut;
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    private StatutFormation statusFormation;

    private Integer nombreJours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id")
    private Formateur formateur;

    @OneToMany(mappedBy = "planFormation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Apprenant> apprenants = new ArrayList<>();


    @OneToMany(mappedBy = "planFormation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Evaluation> evaluations = new ArrayList<>();


}
