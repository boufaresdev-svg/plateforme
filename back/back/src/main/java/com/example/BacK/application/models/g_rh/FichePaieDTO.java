package com.example.BacK.application.models.g_rh;

import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.domain.g_RH.enumEntity.StatutFichePaie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FichePaieDTO {

    private String id;
    private LocalDate dateEmission;
    private double salaireDeBase;
    private double primes;
    private double retenues;
    private double netAPayer;
    private StatutFichePaie statut;
    private EmployeeDTO employee;
}
