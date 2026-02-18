package com.example.BacK.application.g_Vehicule.Command.reparation.addReparation;

import com.example.BacK.domain.g_Vehicule.enumEntity.TypeReparation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddReparationCommand {
    private String url;
    private TypeReparation type;
    private Double prix;
    private LocalDate date;
    private String description;
    private String vehiculeId;
}
