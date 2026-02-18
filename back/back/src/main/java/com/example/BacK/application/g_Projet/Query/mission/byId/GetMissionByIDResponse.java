package com.example.BacK.application.g_Projet.Query.mission.byId;

import com.example.BacK.application.models.g_projet.*;
import com.example.BacK.application.models.g_rh.EmployeeDTO;
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
public class GetMissionByIDResponse {

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
    private EmployeeDTO employee;


}
