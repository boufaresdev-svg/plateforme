package com.example.BacK.application.g_RH.Command.employee.addEmployee;

import com.example.BacK.application.models.g_rh.CongeeDTO;
import com.example.BacK.application.models.g_rh.PrimeDTO;
import com.example.BacK.application.models.g_rh.RetenueDTO;
import com.example.BacK.domain.g_RH.Congee;
import com.example.BacK.domain.g_RH.enumEntity.SituationFamiliale;
import com.example.BacK.domain.g_RH.enumEntity.StatutEmployee;
import com.example.BacK.domain.g_RH.enumEntity.TypePieceIdentite;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeCommand {


    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String poste;
    private String departement;
    private LocalDate dateEmbauche;
    private double salaire;
    private int soldeConges;
    private int congesUtilises;
    private int soldePoints;
    private int pointsDemandesParAn;
    private String adresse;
    private TypePieceIdentite typePieceIdentite;
    private String numeroPieceIdentite;
    private int nombreEnfants;

}
