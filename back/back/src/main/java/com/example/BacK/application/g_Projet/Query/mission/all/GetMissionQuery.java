package com.example.BacK.application.g_Projet.Query.mission.all;

import com.example.BacK.domain.g_Projet.enumEntity.PrioriteMission;
import com.example.BacK.domain.g_Projet.enumEntity.StatutMission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMissionQuery {
    private String id;
    private String nom;
    private String description;
    private String objectif;
    private StatutMission statut;
    private PrioriteMission priorite;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;
    private Double budget;
    private String projet;
    private String phase;

}
