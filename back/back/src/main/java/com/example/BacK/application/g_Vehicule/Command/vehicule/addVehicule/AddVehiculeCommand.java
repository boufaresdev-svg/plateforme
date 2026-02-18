package com.example.BacK.application.g_Vehicule.Command.vehicule.addVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IAddVehiculeCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@IAddVehiculeCommand
public class AddVehiculeCommand {


    private String serie;
    private String marque;
    private LocalDate dateVisiteTechnique;
    private LocalDate dateAssurance;
    private LocalDate dateTaxe;
    private Double prochainVidangeKm;
    private Double kmActuel ;
    private Double prochaineChaineKm ;
    private double consommation100km ;
    private LocalDate dateChangementBatterie;


}
