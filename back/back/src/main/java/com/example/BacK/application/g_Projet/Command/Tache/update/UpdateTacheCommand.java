package com.example.BacK.application.g_Projet.Command.Tache.update;

import com.example.BacK.application.models.g_projet.ChargeDTO;
import com.example.BacK.application.models.g_projet.CommentaireTacheDTO;
import com.example.BacK.application.models.g_projet.EmployeAffecteDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteTache;
import com.example.BacK.domain.g_Projet.enumEntity.StatutTache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTacheCommand {
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
    private String missionId;



}
