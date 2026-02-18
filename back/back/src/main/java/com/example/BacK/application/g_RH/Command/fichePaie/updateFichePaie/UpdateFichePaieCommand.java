package com.example.BacK.application.g_RH.Command.fichePaie.updateFichePaie;

import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.domain.g_RH.enumEntity.StatutFichePaie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFichePaieCommand {
    private String id;
    private LocalDate dateEmission;
    private double salaireDeBase;
    private double primes;
    private double retenues;
    private double netAPayer;
    private StatutFichePaie statut;
    private EmployeeDTO employee;
}
