package com.example.BacK.application.models.g_projet;

import com.example.BacK.application.models.g_rh.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChargeDTO {
    private String id;
    private String nom;
    private double montant;
    private String description;
    private TacheDTO tache;
    private EmployeeDTO employe;
    private String categorie ;
    private String sousCategorie;
}
