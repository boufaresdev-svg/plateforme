package com.example.BacK.application.g_Projet.Query.phase.all;

import com.example.BacK.application.models.g_projet.ProjetDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PhaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPhaseQuery {
    private String id;
    private String nom;
    private String description;
    private Integer ordre;
    private PhaseType statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;
    private Double budget;
    private ProjetDTO projet;

}
