package com.example.BacK.application.g_Vehicule.Command.vehicule.UpdateVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IUpdateVehiculeCommand;
import com.example.BacK.application.models.g_vehicule.ReparationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@IUpdateVehiculeCommand
public class UpdateVehiculeCommand {
    private String id;
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
    private List<ReparationDTO> reparations;
}
