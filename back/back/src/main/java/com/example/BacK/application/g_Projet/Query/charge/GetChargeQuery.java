package com.example.BacK.application.g_Projet.Query.charge;

import com.example.BacK.application.models.g_projet.TacheDTO;
import com.example.BacK.application.models.g_rh.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetChargeQuery {
    private String nom;
    private double montant;
    private String description;
    private String tache;
    private String employe;
    private String categorie ;
    private String sousCategorie;
}
