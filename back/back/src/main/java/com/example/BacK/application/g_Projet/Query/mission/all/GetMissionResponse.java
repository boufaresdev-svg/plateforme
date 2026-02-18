package com.example.BacK.application.g_Projet.Query.mission.all;

import com.example.BacK.application.models.g_projet.EmployeAffecteDTO;
import com.example.BacK.application.models.g_projet.PhaseDTO;
import com.example.BacK.application.models.g_projet.ProjetDTO;
import com.example.BacK.application.models.g_projet.TacheDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteMission;
import com.example.BacK.domain.g_Projet.enumEntity.StatutMission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMissionResponse {
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
    private ProjetDTO projet;
    private PhaseDTO phase;
    private List<TacheDTO> taches;
    private List<EmployeAffecteDTO> employesAffectes;

}
