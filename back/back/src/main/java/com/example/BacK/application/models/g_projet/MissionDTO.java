package com.example.BacK.application.models.g_projet;

import com.example.BacK.domain.g_Projet.EmployeAffecte;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteMission;
import com.example.BacK.domain.g_Projet.enumEntity.StatutMission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MissionDTO {

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
