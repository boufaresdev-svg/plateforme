package com.example.BacK.application.g_Vehicule.Query.Reparation;

import com.example.BacK.application.models.g_vehicule.VehiculeDTO;
import com.example.BacK.domain.g_Vehicule.enumEntity.TypeReparation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetReparationResponse {
    private String id;
    private String url;
    private String vehicleId;
    private TypeReparation type;
    private Double prix;
    private LocalDate date;
    private String description;
    private VehiculeDTO vehicule;
}
