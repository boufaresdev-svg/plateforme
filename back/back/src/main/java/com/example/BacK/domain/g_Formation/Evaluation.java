package com.example.BacK.domain.g_Formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"planFormation", "contenuJourFormation", "apprenant"})
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluation;
    private String type;
    private Date date;
    private String description;
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_formation_id")
    private PlanFormation planFormation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_jour_id")
    private ContenuJourFormation contenuJourFormation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id")
    private Apprenant apprenant;
}
