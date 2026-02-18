package com.example.BacK.application.models.g_rh;

import com.example.BacK.domain.g_RH.Congee;
import com.example.BacK.domain.g_RH.Prime;
import com.example.BacK.domain.g_RH.Retenue;
import com.example.BacK.domain.g_RH.enumEntity.TypePieceIdentite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDTO {


    private String id;
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
    private List<CongeeDTO> conges;
    private List<PrimeDTO> primes;
    private List<RetenueDTO> retenues;

}
