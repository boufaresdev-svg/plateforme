package com.example.BacK.application.g_RH.Command.congee.updateCongee;

import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.domain.g_RH.enumEntity.StatutConge;
import com.example.BacK.domain.g_RH.enumEntity.TypeConge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCongeeCommand {
    private String id;
    private TypeConge type;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int nombreJours;
    private String motif;
    private StatutConge statut;
    private LocalDate dateCreation;
    private LocalDate dateValidation;
    private String validePar;
    private Employee employee;
}
