package com.example.BacK.application.g_Projet.Command.EmployeAffecte.add;

import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_Projet.enumEntity.RoleProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutAffectation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeAffecteCommand {

    private String employeId;
    private RoleProjet role;
    private LocalDate dateAffectation;
    private Double tauxHoraire;
    private Double heuresAllouees;
    private Double heuresRealisees;
    private StatutAffectation statut;
    private String missionId;
    private String tacheId;


}
