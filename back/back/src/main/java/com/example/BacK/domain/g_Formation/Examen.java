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
@ToString(exclude = {"apprenant", "certificat", "planFormation"})
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamen;

    private String type;
    private Date date;
    private String description;
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id")
    private Apprenant apprenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_formation_id")
    private PlanFormation planFormation;

    @OneToOne(mappedBy = "examen", cascade = CascadeType.ALL)
    private Certificat certificat;
}
