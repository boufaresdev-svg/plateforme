package com.example.BacK.application.g_RH.Query.congee;

import com.example.BacK.application.models.g_rh.CongeeDTO;
import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.application.models.g_rh.PrimeDTO;
import com.example.BacK.application.models.g_rh.RetenueDTO;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.domain.g_RH.enumEntity.StatutConge;
import com.example.BacK.domain.g_RH.enumEntity.TypeConge;
import com.example.BacK.domain.g_RH.enumEntity.TypePieceIdentite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCongeeResponse {
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
