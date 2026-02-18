package com.example.BacK.application.models.g_rh;

import com.example.BacK.domain.g_RH.enumEntity.StatutConge;
import com.example.BacK.domain.g_RH.enumEntity.TypeConge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CongeeDTO {

    private String id;
     private TypeConge type;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int nombreJours;
    private String motif;
    private StatutConge statut;
    private LocalDate dateValidation;
    private String validePar;
    private EmployeeDTO employee;
}
