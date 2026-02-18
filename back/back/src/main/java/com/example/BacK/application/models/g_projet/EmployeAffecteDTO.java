package com.example.BacK.application.models.g_projet;

import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_Projet.enumEntity.RoleProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutAffectation;

import com.example.BacK.domain.g_RH.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeAffecteDTO {

    private String id;
    private Employee  employee;
    private String email;
    private RoleProjet role;
    private LocalDate dateAffectation;
    private Double tauxHoraire;
    private Double heuresAllouees;
    private Double heuresRealisees;
    private StatutAffectation statut;
    private TacheDTO tache;
    private MissionDTO mission;
}
