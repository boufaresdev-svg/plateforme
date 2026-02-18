package com.example.BacK.application.models.g_projet;


import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteTache;
import com.example.BacK.domain.g_Projet.enumEntity.StatutTache;
import com.example.BacK.domain.g_RH.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TacheDTO {

    private String id;
    private String nom;
    private String description;
    private StatutTache statut;
    private PrioriteTache priorite;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double dureeEstimee;
    private Double dureeReelle;
    private Double progression;
    private MissionDTO mission;
    private EmployeeDTO employee;

    private List<CommentaireTacheDTO> commentaires;
    private List<ChargeDTO> charges;
    private List<LivrableTacheDTO> livrable;
    private List<ProblemeTacheDTO> probleme;

}
