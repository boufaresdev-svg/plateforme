package com.example.BacK.application.models.g_rh;

import com.example.BacK.domain.g_RH.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RetenueDTO {

    private String id;
    private String libelle;
    private double montant;
    private int nombrePoints;
    private String description;
    private EmployeeDTO employee;
}
