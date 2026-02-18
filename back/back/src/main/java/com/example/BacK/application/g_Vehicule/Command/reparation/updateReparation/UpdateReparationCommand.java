package com.example.BacK.application.g_Vehicule.Command.reparation.updateReparation;

import com.example.BacK.domain.g_Vehicule.enumEntity.TypeReparation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReparationCommand {

    private String id;
    private String url;
    private String vehicleId;
    private TypeReparation type;
    private Double prix;
    private LocalDate date;
    private String description;
    private String vehiculeId;
}