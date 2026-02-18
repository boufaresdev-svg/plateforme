package com.example.BacK.application.g_Vehicule.Command.prix_Carburant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrixCarburantCommand {
    private String id;
    private double essence;
    private double gasoil;
    private double gasoil50;
    private double gpl;
}
