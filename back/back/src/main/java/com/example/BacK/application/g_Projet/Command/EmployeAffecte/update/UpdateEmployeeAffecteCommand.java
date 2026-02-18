package com.example.BacK.application.g_Projet.Command.EmployeAffecte.update;

import com.example.BacK.domain.g_Projet.enumEntity.RoleProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutAffectation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeAffecteCommand {
    private String id;
    private String employeId;
    private String nom;
    private String prenom;
    private String poste;
    private String email;
    private RoleProjet role;
    private LocalDate dateAffectation;
    private Double tauxHoraire;
    private Double heuresAllouees;
    private Double heuresRealisees;
    private StatutAffectation statut;
    private String tache;
    private String mission;
}
